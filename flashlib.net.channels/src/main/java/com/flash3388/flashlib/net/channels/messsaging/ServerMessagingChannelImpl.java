package com.flash3388.flashlib.net.channels.messsaging;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.NetAddress;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.net.channels.NetClient;
import com.flash3388.flashlib.net.channels.NetServerChannel;
import com.flash3388.flashlib.net.channels.nio.ChannelListener;
import com.flash3388.flashlib.net.channels.nio.ChannelOpenListener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public class ServerMessagingChannelImpl implements ServerMessagingChannel {

    private final NetChannelOpener<? extends NetServerChannel> mChannelOpener;
    private final ChannelUpdater mChannelUpdater;
    private final KnownMessageTypes mMessageTypes;
    private final ChannelId mOurId;
    private final Clock mClock;
    private final Logger mLogger;

    private final AtomicReference<ChannelData> mChannel;
    private final AtomicReference<Listener> mListener;
    private final Map<SelectionKey, ClientNode> mClients;
    private final ChannelListener mServerChannelListener;
    private final ChannelOpenListener<NetServerChannel> mOpenListener;
    private boolean mStarted;
    private boolean mClosed;

    public ServerMessagingChannelImpl(NetChannelOpener<? extends NetServerChannel> channelOpener,
                                      ChannelUpdater channelUpdater,
                                      KnownMessageTypes messageTypes,
                                      ChannelId ourId,
                                      Clock clock,
                                      Logger logger) {
        mChannelOpener = channelOpener;
        mChannelUpdater = channelUpdater;
        mMessageTypes = messageTypes;
        mOurId = ourId;
        mClock = clock;
        mLogger = logger;

        messageTypes.put(PingMessage.TYPE);

        mChannel = new AtomicReference<>();
        mListener = new AtomicReference<>();
        mClients = new HashMap<>();
        mServerChannelListener = new ServerChannelListenerImpl(this, logger);
        mOpenListener = new OpenListenerImpl(this, logger);
        mStarted = false;
        mClosed = false;
    }

    @Override
    public void setListener(Listener listener) {
        if (mStarted) {
            throw new IllegalStateException("should not set listener after already started");
        }
        if (mClosed) {
            throw new IllegalStateException("closed");
        }

        mListener.set(listener);
    }

    @Override
    public void start() {
        if (mStarted) {
            throw new IllegalStateException("already started");
        }
        if (mClosed) {
            throw new IllegalStateException("closed");
        }

        startOpenChannel();
        mStarted = true;
    }

    @Override
    public void queue(Message message) {
        if (!mStarted) {
            throw new IllegalStateException("not started");
        }
        if (mClosed) {
            throw new IllegalStateException("closed");
        }

        mLogger.debug("Queuing new message for all clients");

        SendRequest request = new SendRequest(message, false);
        synchronized (mClients) {
            for (ClientNode node : mClients.values()) {
                synchronized (node.outQueue) {
                    node.outQueue.add(request);
                    node.getRegistration().requestReadWriteUpdates();
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (mClosed) {
            return;
        }

        mClosed = true;

        ChannelData channel = mChannel.getAndSet(null);
        if (channel != null) {
            Closeables.silentClose(channel.channel);
        }
    }

    private void startOpenChannel() {
        mLogger.debug("Requesting open channel");
        mChannelUpdater.requestOpenChannel(mChannelOpener, mOpenListener, mServerChannelListener);
    }

    private void onChannelOpen(NetServerChannel channel, UpdateRegistration registration) {
        mLogger.debug("Channel opened");
        mChannel.set(new ChannelData(channel, registration));
    }

    private void onNewClient(NetClient client) {
        UpdateRegistration registration = null;
        try {
            ClientNode node = new ClientNode(client);
            ChannelController channelController = new ClientChannelControllerImpl(this, node, mLogger);
            ChannelListener channelListener = new BaseChannelListener(
                    channelController,
                    mMessageTypes,
                    mOurId,
                    mClock,
                    mLogger,
                    mChannelOpener.isTargetChannelStreaming());

            registration = client.register(mChannelUpdater, channelListener);
            node.registration.set(registration);

            mLogger.debug("New client connected {}", node);

            synchronized (mClients) {
                mClients.put(registration.getKey(), node);
            }

            synchronized (node.outQueue) {
                registration.requestReadWriteUpdates();
            }
        } catch (Throwable t) {
            mLogger.error("Error while trying to handle new client", t);
            Closeables.silentClose(client);

            if (registration != null) {
                try {
                    registration.cancel();
                } catch (Throwable ignore) {}
            }
        }
    }

    private void onNewMessage(ClientNode sender, MessageHeader header, Message message) {
        mLogger.debug("New message received: client={}, sender={}, type={}",
                sender,
                header.getSender(),
                header.getMessageType());

        Listener listener = mListener.get();
        if (!sender.isRegistered()) {
            sender.id.set(header.getSender());

            // client is now registered, handle any queued messages.
            sender.getRegistration().requestReadWriteUpdates();

            mLogger.debug("First message from client {}, setting id={}",
                    sender, header.getSender());

            if (listener != null) {
                try {
                    listener.onClientConnected(sender.getId());
                } catch (Throwable ignored) {}
            }
        }

        if (message.getType().equals(PingMessage.TYPE)) {
            mLogger.debug("ServerChannel: received ping message, responding");

            // resend the ping message with the same time
            synchronized (sender.outQueue) {
                sender.outQueue.add(new SendRequest(message, false));
                sender.getRegistration().requestReadWriteUpdates();
            }

            return;
        }

        if (listener != null) {
            try {
                listener.onNewMessage(header, message);
            } catch (Throwable ignore) {}
        }

        if (header.isOnlyForServer()) {
            return;
        }

        mLogger.debug("Directing new message to all other clients");

        SendRequest request = new SendRequest(header, message);
        synchronized (mClients) {
            for (ClientNode node : mClients.values()) {
                if (node.getRegistration().getKey().equals(sender.getRegistration().getKey())) {
                    continue;
                }

                synchronized (node.outQueue) {
                    node.outQueue.add(request);
                    node.getRegistration().requestReadWriteUpdates();
                }
            }
        }
    }

    private void disconnectClient(ClientNode node) {
        mLogger.debug("Disconnecting client {}", node);

        SelectionKey key = node.getRegistration().getKey();
        synchronized (mClients) {
            mClients.remove(key);
        }

        Closeables.silentClose(node.client);
        try {
            node.getRegistration().cancel();
        } catch (Throwable ignore) {}

        Listener listener = mListener.get();
        if (listener != null && node.isRegistered()) {
            // if id is not null, then we did not report this channel as connected,
            // and such we will not report it as disconnected
            listener.onClientDisconnected(node.getId());
        }
    }

    private static class ServerChannelListenerImpl implements ChannelListener {

        private final WeakReference<ServerMessagingChannelImpl> mChannel;
        private final Logger mLogger;

        private ServerChannelListenerImpl(ServerMessagingChannelImpl channel, Logger logger) {
            mChannel = new WeakReference<>(channel);
            mLogger = logger;
        }

        @Override
        public void onAcceptable(SelectionKey key) {
            ServerMessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ChannelData channel = ourChannel.mChannel.get();
            if (channel == null) {
                mLogger.warn("Received accept update but channel is closed");
                return;
            }

            try {
                NetClient client = channel.channel.acceptNewClient();
                ourChannel.onNewClient(client);
            } catch (IOException e) {
                mLogger.error("Error accepting new client", e);
            }
        }

        @Override
        public void onConnectable(SelectionKey key) {
            // unused
        }

        @Override
        public void onReadable(SelectionKey key) {
            // unused
        }

        @Override
        public void onWritable(SelectionKey key) {
            // unused
        }

        @Override
        public void onRequestedUpdate(SelectionKey key, Object param) {
            // unused
        }
    }

    private static class ClientChannelControllerImpl implements ChannelController {

        private final WeakReference<ServerMessagingChannelImpl> mChannel;
        private final ClientNode mNode; // weak reference?
        private final Logger mLogger;

        private ClientChannelControllerImpl(ServerMessagingChannelImpl channel, ClientNode node, Logger logger) {
            mChannel = new WeakReference<>(channel);
            mNode = node;
            mLogger = logger;
        }

        @Override
        public NetChannel getChannel() {
            return mNode.client;
        }

        @Override
        public SendRequest getNextSendRequest() {
            if (mNode.getRegistration() == null) {
                mLogger.warn("next update requested when update registration not initialized");
                return null;
            }

            if (!mNode.isRegistered()) {
                mLogger.warn("next update requested when client not registered");

                // not registered yet, don't send anything
                synchronized (mNode.outQueue) {
                    mNode.getRegistration().requestReadUpdates();
                }

                return null;
            }

            SendRequest request;
            synchronized (mNode.outQueue) {
                request = mNode.outQueue.poll();
                if (request == null) {
                    mNode.getRegistration().requestReadUpdates();
                    return null;
                }
            }

            return request;
        }

        @Override
        public void resetChannel() {
            if (mNode.getRegistration() == null) {
                mLogger.warn("reset requested when update registration not initialized");
                return;
            }

            ServerMessagingChannelImpl ourChannel = getBaseChannel();
            if (ourChannel == null) {
                return;
            }

            ourChannel.disconnectClient(mNode);
        }

        @Override
        public void onNewMessage(NetAddress address, MessageHeader header, Message message) {
            ServerMessagingChannelImpl ourChannel = getBaseChannel();
            if (ourChannel == null) {
                return;
            }

            ourChannel.onNewMessage(mNode, header, message);
        }

        @Override
        public void onMessageSendingFailed(Message message, Throwable cause) {
            ServerMessagingChannelImpl ourChannel = getBaseChannel();
            if (ourChannel == null) {
                return;
            }

            Listener listener = ourChannel.mListener.get();
            if (listener != null) {
                try {
                    listener.onMessageSendingFailed(mNode.getId(), message, cause);
                } catch (Throwable ignore) {}
            }
        }

        @Override
        public void onChannelCustomUpdate(Object param) {

        }

        @Override
        public void onChannelConnectable() {

        }

        private ServerMessagingChannelImpl getBaseChannel() {
            ServerMessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return null;
            }

            return ourChannel;
        }
    }

    private static class OpenListenerImpl implements ChannelOpenListener<NetServerChannel> {

        private final WeakReference<ServerMessagingChannelImpl> mChannel;
        private final Logger mLogger;

        private OpenListenerImpl(ServerMessagingChannelImpl channel, Logger logger) {
            mChannel = new WeakReference<>(channel);
            mLogger = logger;
        }

        @Override
        public void onOpen(NetServerChannel channel, UpdateRegistration registration) {
            ServerMessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.onChannelOpen(channel, registration);
        }

        @Override
        public void onError(Throwable t) {
            ServerMessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.startOpenChannel();
        }
    }

    private static class ClientNode {
        public final NetClient client;
        public final Queue<SendRequest> outQueue;
        public final AtomicReference<UpdateRegistration> registration;
        public final AtomicReference<ChannelId> id;

        private ClientNode(NetClient client) {
            this.client = client;
            this.outQueue = new ArrayDeque<>();
            this.registration = new AtomicReference<>();
            this.id = new AtomicReference<>();
        }

        public UpdateRegistration getRegistration() {
            return registration.get();
        }

        public ChannelId getId() {
            return id.get();
        }

        public boolean isRegistered() {
            return getId() != null;
        }

        @Override
        public String toString() {
            return client.getAddress().toString();
        }
    }

    private static class ChannelData {
        public final NetServerChannel channel;
        public final UpdateRegistration registration;

        private ChannelData(NetServerChannel channel, UpdateRegistration registration) {
            this.channel = channel;
            this.registration = registration;
        }
    }
}
