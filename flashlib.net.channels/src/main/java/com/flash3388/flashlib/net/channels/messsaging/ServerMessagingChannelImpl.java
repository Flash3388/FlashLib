package com.flash3388.flashlib.net.channels.messsaging;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.IncomingData;
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
import com.flash3388.flashlib.time.Time;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public class ServerMessagingChannelImpl implements ServerMessagingChannel {

    private final NetChannelOpener<? extends NetServerChannel> mChannelOpener;
    private final ChannelUpdater mChannelUpdater;
    private final ChannelId mOurId;
    private final Logger mLogger;

    private final AtomicReference<ChannelData> mChannel;
    private final AtomicReference<Listener> mListener;
    private final Map<SelectionKey, ClientNode> mClients;
    private final ChannelListener mServerChannelListener;
    private final ChannelListener mClientsChannelListener;
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
        mOurId = ourId;
        mLogger = logger;

        messageTypes.put(PingMessage.TYPE);

        mChannel = new AtomicReference<>();
        mListener = new AtomicReference<>();
        mClients = new HashMap<>();
        mServerChannelListener = new ServerChannelListenerImpl(this, logger);
        mClientsChannelListener = new ClientChannelListenerImpl(this, messageTypes, ourId, clock, logger);
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

        SendRequest request = new SendRequest(message, null, mOurId);
        synchronized (mClients) {
            for (ClientNode node : mClients.values()) {
                synchronized (node.outQueue) {
                    node.outQueue.add(request);
                    node.registration.requestReadWriteUpdates();
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
            registration = client.register(mChannelUpdater, mClientsChannelListener);

            ClientNode node = new ClientNode(client, registration);
            mLogger.debug("New client connected {}", node);

            synchronized (mClients) {
                mClients.put(registration.getKey(), node);
            }

            registration.requestReadWriteUpdates();
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
            sender.id = header.getSender();

            // client is now registered, handle any queued messages.
            sender.registration.requestReadWriteUpdates();

            mLogger.debug("First message from client {}, setting id={}",
                    sender, header.getSender());

            if (listener != null) {
                try {
                    listener.onClientConnected(sender.id);
                } catch (Throwable ignored) {}
            }
        }

        if (message.getType().equals(PingMessage.TYPE)) {
            mLogger.debug("ServerChannel: received ping message, responding");

            // resend the ping message with the same time
            synchronized (sender.outQueue) {
                sender.outQueue.add(new SendRequest(message, null, mOurId));
                sender.registration.requestReadWriteUpdates();
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

        SendRequest request = new SendRequest(message, header, header.getSender());
        synchronized (mClients) {
            for (ClientNode node : mClients.values()) {
                if (node.registration.getKey().equals(sender.registration.getKey())) {
                    continue;
                }

                synchronized (node.outQueue) {
                    node.outQueue.add(request);
                    node.registration.requestReadWriteUpdates();
                }
            }
        }
    }

    private void disconnectClient(SelectionKey key, ClientNode node) {
        mLogger.debug("Disconnecting client {}", node);

        synchronized (mClients) {
            mClients.remove(key);
        }

        Closeables.silentClose(node.client);
        try {
            node.registration.cancel();
        } catch (Throwable ignore) {}

        Listener listener = mListener.get();
        if (listener != null && node.isRegistered()) {
            // if id is not null, then we did not report this channel as connected,
            // and such we will not report it as disconnected
            listener.onClientDisconnected(node.id);
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

    private static class ClientChannelListenerImpl implements ChannelListener {

        private final WeakReference<ServerMessagingChannelImpl> mChannel;
        private final Clock mClock;
        private final Logger mLogger;

        private final MessageReadingContext mReadingContext;
        private final MessageSerializer mSerializer;
        private final List<SendRequest> mWriteQueueLocal;

        private ClientChannelListenerImpl(ServerMessagingChannelImpl channel,
                                          KnownMessageTypes messageTypes,
                                          ChannelId ourId,
                                          Clock clock,
                                          Logger logger) {
            mChannel = new WeakReference<>(channel);
            mClock = clock;
            mLogger = logger;

            mReadingContext = new MessageReadingContext(messageTypes, logger);
            mSerializer = new MessageSerializer(ourId);
            mWriteQueueLocal = new ArrayList<>();
        }

        @Override
        public void onAcceptable(SelectionKey key) {
            // unused
        }

        @Override
        public void onConnectable(SelectionKey key) {
            // unused
        }

        @Override
        public void onReadable(SelectionKey key) {
            ServerMessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ClientNode client;
            synchronized (ourChannel.mClients) {
                client = ourChannel.mClients.get(key);
            }

            if (client == null) {
                // client was likely closed
                mLogger.warn("Received read update on non-existing client {}", key);
                return;
            }

            try {
                IncomingData data = mReadingContext.readFromChannel(client.client);
                if (data.getBytesReceived() >= 1) {
                    mLogger.debug("New data from remote: {}, size={}",
                            data.getSender(),
                            data.getBytesReceived());
                }

                parseMessages(ourChannel, client);
            } catch (ClosedChannelException e) {
                mLogger.debug("Client channel reported closed");
                ourChannel.disconnectClient(key, client);
            } catch (IOException e) {
                mLogger.error("Error while reading and processing new data", e);
                ourChannel.disconnectClient(key, client);
            }
        }

        @Override
        public void onWritable(SelectionKey key) {
            ServerMessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ClientNode client;
            synchronized (ourChannel.mClients) {
                client = ourChannel.mClients.get(key);
            }

            if (client == null) {
                // client was likely closed
                mLogger.warn("Received write update on non-existing client {}", key);
                return;
            }

            if (!client.isRegistered()) {
                // not registered yet, don't send anything
                client.registration.requestReadUpdates();
                return;
            }

            mWriteQueueLocal.clear();
            synchronized (client.outQueue) {
                mWriteQueueLocal.addAll(client.outQueue);
                client.outQueue.clear();

                client.registration.requestReadUpdates();
            }

            for (SendRequest request : mWriteQueueLocal) {
                MessageSerializer.SerializedMessage serialized;
                try {
                    if (request.header != null) {
                        serialized = mSerializer.serialize(request.header, request.message);
                    } else {
                        Time now = mClock.currentTime();
                        serialized = mSerializer.serialize(now, request.message, false);
                    }
                } catch (IOException e) {
                    mLogger.error("Error serializing message data", e);

                    Listener listener = ourChannel.mListener.get();
                    if (listener != null) {
                        try {
                            listener.onMessageSendingFailed(request.senderId, request.message);
                        } catch (Throwable ignore) {}
                    }

                    continue;
                }

                try {
                    mLogger.debug("Writing data to channel: client={}, size={}", client, serialized.getSize());
                    serialized.writeInto(client.client);
                } catch (IOException e) {
                    mLogger.error("Error while writing data", e);

                    // todo: we may not want to reset connection on every failure
                    ourChannel.disconnectClient(key, client);
                    break;
                }
            }
        }

        @Override
        public void onRequestedUpdate(SelectionKey key, Object param) {
            // unused
        }

        private void parseMessages(ServerMessagingChannelImpl ourChannel, ClientNode sender) throws IOException {
            boolean hasMoreToParse;
            do {
                Optional<MessageReadingContext.ParseResult> resultOptional = mReadingContext.parse();
                if (resultOptional.isPresent()) {
                    MessageReadingContext.ParseResult parseResult = resultOptional.get();

                    MessageHeader header = parseResult.getHeader();
                    Message message = parseResult.getMessage();

                    ourChannel.onNewMessage(sender, header, message);

                    hasMoreToParse = true;
                } else {
                    hasMoreToParse = false;
                }
            } while (hasMoreToParse);
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
        public final UpdateRegistration registration;
        public final Queue<SendRequest> outQueue;
        public ChannelId id;

        private ClientNode(NetClient client, UpdateRegistration registration) {
            this.client = client;
            this.registration = registration;
            this.outQueue = new ArrayDeque<>();
            this.id = null;
        }

        public boolean isRegistered() {
            return id != null;
        }

        @Override
        public String toString() {
            return client.getInfo().toString();
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

    private static class SendRequest {
        public final Message message;
        public final MessageHeader header;
        public final ChannelId senderId;

        private SendRequest(Message message, MessageHeader header, ChannelId senderId) {
            this.message = message;
            this.header = header;
            this.senderId = senderId;
        }
    }
}
