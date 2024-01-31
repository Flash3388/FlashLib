package com.flash3388.flashlib.net.channels.messsaging;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.ConnectableNetChannel;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
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
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public class MessagingChannelImpl implements MessagingChannel {

    private static final Time OPEN_DELAY = Time.seconds(1);

    enum UpdateRequest {
        START_CONNECT,
        PING_CHECK
    }

    private final NetChannelOpener<? extends ConnectableNetChannel> mChannelOpener;
    private final ChannelUpdater mChannelUpdater;
    private final SocketAddress mRemoteAddress;
    private final ServerClock mClock;
    private final Logger mLogger;

    private final AtomicReference<ChannelData> mChannel;
    private final AtomicReference<Listener> mListener;
    private final Queue<SendRequest> mQueue;
    private final PingContext mPingContext;
    private final ChannelListenerImpl mChannelListener;
    private final ChannelOpenListener<ConnectableNetChannel> mOpenListener;

    public MessagingChannelImpl(NetChannelOpener<? extends ConnectableNetChannel> channelOpener,
                                ChannelUpdater channelUpdater,
                                SocketAddress remoteAddress,
                                KnownMessageTypes messageTypes,
                                ChannelId ourId,
                                Clock clock,
                                Logger logger) {
        mChannelOpener = channelOpener;
        mChannelUpdater = channelUpdater;
        mRemoteAddress = remoteAddress;
        mClock = new ServerClock(clock, logger);
        mLogger = logger;

        messageTypes.put(PingMessage.TYPE);

        mChannel = new AtomicReference<>();
        mListener = new AtomicReference<>();
        mQueue = new LinkedList<>();
        mPingContext = new PingContext(this, mClock, logger);
        mChannelListener = new ChannelListenerImpl(this, messageTypes, ourId, mClock, logger);
        mOpenListener = new OpenListenerImpl(this, logger);
    }

    @Override
    public void setListener(Listener listener) {
        mListener.set(listener);
    }

    @Override
    public void enableKeepAlive() {
        mPingContext.enable();
    }

    @Override
    public void start() {
        // todo: should not be called more then once
        startChannelOpen(false);
    }

    @Override
    public void resetConnection() {
        // todo: if this is done during channel open procedure, then it causes problems
        Closeables.silentClose(this);
        startChannelOpen(true);
    }

    @Override
    public void queue(Message message) {
        queue(message, false);
    }

    public void queue(Message message, boolean onlyForServer) {
        mLogger.debug("Queuing new message");

        synchronized (mQueue) {
            ChannelData data = mChannel.get();
            mQueue.add(new SendRequest(message, onlyForServer));

            if (data != null && data.isConnected) {
                data.registration.requestReadWriteUpdates();
            }
        }
    }

    @Override
    public void close() throws IOException {
        ChannelData data = mChannel.getAndSet(null);
        if (data == null) {
            return;
        }

        try {
            data.registration.cancel();
        } catch (Throwable ignore) {}

        Closeables.silentClose(data.channel);

        mPingContext.onDisconnect();

        Listener listener = mListener.get();
        if (listener != null) {
            try {
                listener.onDisconnect();
            } catch (Throwable ignore) {}
        }
    }

    private void startChannelOpen(boolean delayed) {
        mLogger.debug("Requesting open channel");
        mChannelUpdater.requestOpenChannel(mChannelOpener, mOpenListener, mChannelListener, delayed ? OPEN_DELAY : Time.seconds(0));
    }

    private void onChannelOpen(ConnectableNetChannel channel, UpdateRegistration registration) {
        mLogger.debug("Channel opened");
        mChannel.set(new ChannelData(channel, registration));
        startConnection();
    }

    private void startConnection() {
        ChannelData channel = mChannel.get();
        if (channel == null) {
            mLogger.warn("start connection requested while channel is null");
            return;
        }

        mLogger.debug("Requesting channel connect to {}", mRemoteAddress);
        try {
            // TODO: SUPPORT CHANGING REMOTES
            channel.channel.startConnection(mRemoteAddress);
            channel.registration.requestConnectionUpdates();
        } catch (IOException | RuntimeException | Error e) {
            mLogger.error("Error while trying to start connection, retrying", e);
            retryConnection();
        }
    }

    private void retryConnection() {
        ChannelData channel = mChannel.get();
        if (channel == null) {
            mLogger.warn("retry connection requested while channel is null");
            return;
        }

        mLogger.debug("Requesting retry start connect");
        channel.registration.requestUpdate(UpdateRequest.START_CONNECT);
    }

    private void onConnectionSuccessful() {
        ChannelData channel = mChannel.get();
        if (channel == null) {
            mLogger.warn("connection successful while channel is null");
            return;
        }

        mLogger.debug("Channel connected");

        synchronized (mQueue) {
            channel.isConnected = true;
            channel.registration.requestReadWriteUpdates();
        }

        mPingContext.onConnect();
        channel.registration.requestUpdate(UpdateRequest.PING_CHECK);

        Listener listener = mListener.get();
        if (listener != null) {
            try {
                listener.onConnect();
            } catch (Throwable ignore) {}
        }
    }

    private void onNewMessage(MessageHeader header, Message message) {
        mLogger.debug("New message received: sender={}, type={}",
                header.getSender(),
                header.getMessageType());

        if (message.getType().equals(PingMessage.TYPE)) {
            PingMessage pingMessage = (PingMessage) message;
            mPingContext.onPingResponse(header, pingMessage);
            return;
        }

        // fixed timestamp to client-only times
        Time sendTime = mClock.adjustToClientTime(header.getSendTime());
        MessageHeader newHeader = new MessageHeader(
                header.getContentSize(),
                header.getSender(),
                header.getMessageType(),
                sendTime,
                header.isOnlyForServer()
        );

        Listener listener = mListener.get();
        if (listener != null) {
            try {
                listener.onNewMessage(newHeader, message);
            } catch (Throwable ignore) {}
        }
    }

    private static class ChannelListenerImpl implements ChannelListener {

        private final WeakReference<MessagingChannelImpl> mChannel;
        private final Clock mClock;
        private final Logger mLogger;

        private final MessageReadingContext mReadingContext;
        private final MessageSerializer mSerializer;
        private final List<SendRequest> mWriteQueueLocal;

        private ChannelListenerImpl(MessagingChannelImpl channel,
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
            // not used
        }

        @Override
        public void onConnectable(SelectionKey key) {
            MessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ChannelData channel = ourChannel.mChannel.get();
            if (channel == null) {
                mLogger.warn("Channel is closed but received new connect update");
                return;
            }

            try {
                channel.channel.finishConnection();
                ourChannel.onConnectionSuccessful();
                mReadingContext.clear();
            } catch (IOException e) {
                mLogger.error("Error while trying to connect, retrying", e);

                // todo: add delay before doing this or it will cause a too big a busy loop
                ourChannel.resetConnection();
            }
        }

        @Override
        public void onReadable(SelectionKey key) {
            MessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ChannelData channel = ourChannel.mChannel.get();
            if (channel == null) {
                mLogger.warn("Channel is closed but received new read update");
                return;
            }

            try {
                IncomingData data = mReadingContext.readFromChannel(channel.channel);
                if (data.getBytesReceived() >= 1) {
                    mLogger.debug("New data from remote: {}, size={}",
                            data.getSender(),
                            data.getBytesReceived());
                }

                parseMessages(ourChannel);
            } catch (IOException e) {
                mLogger.error("Error while reading and processing new data", e);
                ourChannel.resetConnection();
            }
        }

        @Override
        public void onWritable(SelectionKey key) {
            MessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ChannelData channel = ourChannel.mChannel.get();
            if (channel == null) {
                mLogger.warn("Channel is closed but received write update");
                return;
            }

            mWriteQueueLocal.clear();
            synchronized (ourChannel.mQueue) {
                mWriteQueueLocal.addAll(ourChannel.mQueue);
                ourChannel.mQueue.clear();

                channel.registration.requestReadUpdates();
            }

            for (SendRequest request : mWriteQueueLocal) {
                MessageSerializer.SerializedMessage serialized;
                try {
                    // todo: improve data usage when serializing and writing
                    Time now = mClock.currentTime();
                    serialized = mSerializer.serialize(now, request.message, request.isOnlyForServer);
                } catch (IOException e) {
                    mLogger.error("Error serializing message data", e);

                    Listener listener = ourChannel.mListener.get();
                    if (listener != null) {
                        try {
                            listener.onMessageSendingFailed(request.message);
                        } catch (Throwable ignore) {}
                    }

                    continue;
                }

                try {
                    mLogger.debug("Writing data to channel: size={}", serialized.getSize());
                    serialized.writeInto(channel.channel);
                } catch (IOException e) {
                    mLogger.error("Error while writing data", e);

                    // todo: we may not want to reset connection on every failure
                    ourChannel.resetConnection();
                    break;
                }
            }
        }

        @Override
        public void onRequestedUpdate(SelectionKey key, Object param) {
            if (!(param instanceof UpdateRequest)) {
                return;
            }

            MessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            UpdateRequest request = (UpdateRequest) param;
            switch (request) {
                case START_CONNECT:
                    ourChannel.startConnection();
                    break;
                case PING_CHECK: {
                    ChannelData channel = ourChannel.mChannel.get();
                    if (channel == null) {
                        mLogger.warn("Channel is closed but received ping check update");
                        break;
                    }

                    if (!channel.isConnected) {
                        mLogger.warn("Channel is not connected but received ping check update");
                        break;
                    }

                    ourChannel.mPingContext.pingIfNecessary();
                    channel.registration.requestUpdate(UpdateRequest.PING_CHECK);
                    break;
                }
                default:
                    break;
            }
        }

        private void parseMessages(MessagingChannelImpl ourChannel) throws IOException {
            boolean hasMoreToParse;
            do {
                Optional<MessageReadingContext.ParseResult> resultOptional = mReadingContext.parse();
                if (resultOptional.isPresent()) {
                    MessageReadingContext.ParseResult parseResult = resultOptional.get();

                    MessageHeader header = parseResult.getHeader();
                    Message message = parseResult.getMessage();

                    ourChannel.onNewMessage(header, message);

                    hasMoreToParse = true;
                } else {
                    hasMoreToParse = false;
                }
            } while (hasMoreToParse);
        }
    }

    private static class OpenListenerImpl implements ChannelOpenListener<ConnectableNetChannel> {

        private final WeakReference<MessagingChannelImpl> mChannel;
        private final Logger mLogger;

        private OpenListenerImpl(MessagingChannelImpl channel, Logger logger) {
            mChannel = new WeakReference<>(channel);
            mLogger = logger;
        }

        @Override
        public void onOpen(ConnectableNetChannel channel, UpdateRegistration registration) {
            MessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.onChannelOpen(channel, registration);
        }

        @Override
        public void onError(Throwable t) {
            MessagingChannelImpl ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.startChannelOpen(true);
        }
    }

    private static class ChannelData {
        public final ConnectableNetChannel channel;
        public final UpdateRegistration registration;
        public boolean isConnected;

        private ChannelData(ConnectableNetChannel channel, UpdateRegistration registration) {
            this.channel = channel;
            this.registration = registration;
            this.isConnected = false;
        }
    }

    private static class SendRequest {
        public final Message message;
        public final boolean isOnlyForServer;

        private SendRequest(Message message, boolean isOnlyForServer) {
            this.message = message;
            this.isOnlyForServer = isOnlyForServer;
        }
    }
}
