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
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
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
    private final Clock mClock;
    private final Logger mLogger;

    private final AtomicReference<ChannelData> mChannel;
    private final AtomicReference<Listener> mListener;
    private final Map<SelectionKey, ClientNode> mClients;
    private final MessageSerializer mSerializer;
    private final ChannelListener mServerChannelListener;
    private final ChannelListener mClientsChannelListener;
    private final ChannelOpenListener<NetServerChannel> mOpenListener;

    public ServerMessagingChannelImpl(NetChannelOpener<? extends NetServerChannel> channelOpener,
                                      ChannelUpdater channelUpdater,
                                      KnownMessageTypes messageTypes,
                                      InstanceId ourId,
                                      Clock clock,
                                      Logger logger) {
        mChannelOpener = channelOpener;
        mChannelUpdater = channelUpdater;
        mClock = clock;
        mLogger = logger;

        mChannel = new AtomicReference<>();
        mListener = new AtomicReference<>();
        mClients = new HashMap<>();
        mSerializer = new MessageSerializer(ourId);
        mServerChannelListener = new ServerChannelListenerImpl(this, logger);
        mClientsChannelListener = new ClientChannelListenerImpl(this, messageTypes, logger);
        mOpenListener = new OpenListenerImpl(this, logger);

        startOpenChannel();
    }

    @Override
    public void setListener(Listener listener) {
        mListener.set(listener);
    }

    @Override
    public void queue(Message message) {
        mLogger.debug("Queuing new message for all clients");

        boolean failed = false;
        try {
            // todo: serialize on when sending
            Time now = mClock.currentTime();
            byte[] content = mSerializer.serialize(now, message, false);

            synchronized (mClients) {
                for (ClientNode node : mClients.values()) {
                    node.outQueue.add(content);
                    node.registration.requestReadWriteUpdates();
                }
            }
        } catch (IOException e) {
            mLogger.error("Failed to queue message", e);
            failed = true;
        }

        if (failed) {
            Listener listener = mListener.get();
            if (listener != null) {
                try {
                    // todo: this can actually lead to stackoverflow
                    //      if it keeps happening
                    listener.onMessageSendingFailed(message);
                } catch (Throwable ignore) {}
            }
        }
    }

    @Override
    public void close() throws IOException {
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
        if (sender.instanceId == null) {
            sender.instanceId = header.getSender();
            mLogger.debug("First message from client {}, setting instanceId={}", sender, header.getSender());

            if (listener != null) {
                // TODO: DON'T USE INSTANCEID AS CLIENTID AS ONE INSTANCE CAN HAVE SEVERAL CLIENTS???
                listener.onClientConnected(sender.instanceId);
            }
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
        try {
            Time now = mClock.currentTime();
            byte[] content = mSerializer.serialize(now, message, false);

            synchronized (mClients) {
                for (ClientNode node : mClients.values()) {
                    if (node.registration.getKey().equals(sender.registration.getKey())) {
                        continue;
                    }

                    node.outQueue.add(content);
                    node.registration.requestReadWriteUpdates();
                }
            }
        } catch (IOException e) {
            // we should report this failure to the listener, as it is internal
            mLogger.error("Error while handling new message", e);
        }
    }

    private void disconnectClient(SelectionKey key, ClientNode node) {
        // todo: how to recognize disconnection made by client, not by us???
        //      will be known on read/write attempt
        //      but how to push it?? key.isvalud???
        mLogger.debug("Disconnecting client {}", node);

        synchronized (mClients) {
            mClients.remove(key);
        }

        Closeables.silentClose(node.client);
        try {
            node.registration.cancel();
        } catch (Throwable ignore) {}

        Listener listener = mListener.get();
        if (listener != null && node.instanceId != null) {
            listener.onClientDisconnected(node.instanceId);
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
        private final Logger mLogger;

        private final MessageReadingContext mReadingContext;
        private final List<byte[]> mWriteQueueLocal;

        private ClientChannelListenerImpl(ServerMessagingChannelImpl channel,
                                          KnownMessageTypes messageTypes,
                                          Logger logger) {
            mChannel = new WeakReference<>(channel);
            mLogger = logger;

            mReadingContext = new MessageReadingContext(messageTypes, logger);
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

            mWriteQueueLocal.clear();
            synchronized (client.outQueue) {
                mWriteQueueLocal.addAll(client.outQueue);
                client.outQueue.clear();

                client.registration.requestReadUpdates();
            }

            for (byte[] buffer : mWriteQueueLocal) {
                try {
                    mLogger.debug("Writing data to channel: client={}, size={}", client, buffer.length);
                    client.client.write(ByteBuffer.wrap(buffer));
                } catch (IOException e) {
                    mLogger.error("Error while writing data", e);

                    // todo: we need to alert on failure to send message
                    //      but we only know the buffer not the message

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
        public final Queue<byte[]> outQueue;
        public InstanceId instanceId;

        private ClientNode(NetClient client, UpdateRegistration registration) {
            this.client = client;
            this.registration = registration;
            this.outQueue = new ArrayDeque<>();
            this.instanceId = null;
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
}
