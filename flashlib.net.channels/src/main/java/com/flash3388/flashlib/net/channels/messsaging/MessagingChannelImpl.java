package com.flash3388.flashlib.net.channels.messsaging;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.ConnectableNetChannel;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
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

    enum UpdateRequest {
        START_CONNECT
    }

    private final NetChannelOpener<? extends ConnectableNetChannel> mChannelOpener;
    private final ChannelUpdater mChannelUpdater;
    private final SocketAddress mRemoteAddress;
    private final Clock mClock;
    private final Logger mLogger;

    private final AtomicReference<ChannelData> mChannel;
    private final AtomicReference<Listener> mListener;
    private final Queue<byte[]> mQueue;
    private final MessageSerializer mSerializer;
    private final ChannelListenerImpl mChannelListener;
    private final ChannelOpenListener<ConnectableNetChannel> mOpenListener;

    public MessagingChannelImpl(NetChannelOpener<? extends ConnectableNetChannel> channelOpener,
                                ChannelUpdater channelUpdater,
                                SocketAddress remoteAddress,
                                KnownMessageTypes messageTypes,
                                InstanceId ourId,
                                Clock clock,
                                Logger logger) {
        mChannelOpener = channelOpener;
        mChannelUpdater = channelUpdater;
        mRemoteAddress = remoteAddress;
        mClock = clock;
        mLogger = logger;

        mChannel = new AtomicReference<>();
        mListener = new AtomicReference<>();
        mQueue = new LinkedList<>();
        mSerializer = new MessageSerializer(ourId);
        mChannelListener = new ChannelListenerImpl(this, messageTypes, logger);
        mOpenListener = new OpenListenerImpl(this, logger);

        startChannelOpen();
    }

    @Override
    public void setListener(Listener listener) {
        mListener.set(listener);
    }

    @Override
    public void resetConnection() {
        Closeables.silentClose(this);
        startChannelOpen();
    }

    @Override
    public void queue(Message message, boolean onlyForServer) {
        mLogger.debug("Queuing new message");

        ChannelData data = mChannel.get();

        boolean failed = false;
        try {
            // todo: serialize on when sending
            Time now = mClock.currentTime();
            byte[] content = mSerializer.serialize(now, message, onlyForServer);

            synchronized (mQueue) {
                mQueue.add(content);

                if (data != null && data.isConnected) {
                    data.registration.requestReadWriteUpdates();
                }
            }
        } catch (IOException e) {
            mLogger.error("Error while trying to send a message", e);
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
        ChannelData data = mChannel.getAndSet(null);
        if (data == null) {
            return;
        }

        try {
            data.registration.cancel();
        } catch (Throwable ignore) {}

        data.channel.close();

        Listener listener = mListener.get();
        if (listener != null) {
            try {
                listener.onDisconnect();
            } catch (Throwable ignore) {}
        }
    }

    private void startChannelOpen() {
        mLogger.debug("Requesting open channel");
        mChannelUpdater.requestOpenChannel(mChannelOpener, mOpenListener, mChannelListener);
    }

    private void onChannelOpen(ConnectableNetChannel channel, UpdateRegistration registration) {
        mLogger.debug("Channel opened");
        mChannel.set(new ChannelData(channel, registration));
        startConnection();
    }

    private void startConnection() {
        ChannelData channel = mChannel.get();
        if (channel == null) {
            // we were just closed
            return;
        }

        mLogger.debug("Requesting channel connect to {}", mRemoteAddress);
        try {
            // TODO: SUPPORT CHANGING REMOTES
            channel.channel.startConnection(mRemoteAddress);
            channel.registration.requestConnectionUpdates();
        } catch (IOException | RuntimeException | Error e) {
            retryConnection();
        }
    }

    private void retryConnection() {
        ChannelData channel = mChannel.get();
        if (channel == null) {
            // we were just closed
            return;
        }

        mLogger.debug("Requesting retry start connect");
        channel.registration.requestUpdate(UpdateRequest.START_CONNECT);
    }

    private void onConnectionSuccessful() {
        ChannelData channel = mChannel.get();
        if (channel == null) {
            // we were just closed
            return;
        }

        mLogger.debug("Channel connected");

        synchronized (mQueue) {
            channel.isConnected = true;
            channel.registration.requestReadWriteUpdates();
        }

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

        Listener listener = mListener.get();
        if (listener != null) {
            try {
                listener.onNewMessage(header, message);
            } catch (Throwable ignore) {}
        }
    }

    private static class ChannelListenerImpl implements ChannelListener {

        private final WeakReference<MessagingChannelImpl> mChannel;
        private final Logger mLogger;

        private final MessageReadingContext mReadingContext;
        private final List<byte[]> mWriteQueueLocal;

        private ChannelListenerImpl(MessagingChannelImpl channel,
                                    KnownMessageTypes messageTypes,
                                    Logger logger) {
            mChannel = new WeakReference<>(channel);
            mLogger = logger;

            mReadingContext = new MessageReadingContext(messageTypes, logger);
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
            } catch (IOException e) {
                mLogger.error("Error while trying to connect, retrying", e);
                ourChannel.retryConnection();
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

            for (byte[] buffer : mWriteQueueLocal) {
                try {
                    mLogger.debug("Writing data to channel: size={}", buffer.length);
                    channel.channel.write(ByteBuffer.wrap(buffer));
                } catch (IOException e) {
                    mLogger.error("Error while writing data", e);

                    // todo: we need to alert on failure to send message
                    //      but we only know the buffer not the message
                    // todo: we lose all messages not send if we reset channel
                    //      now, need to return them to queue

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

            ourChannel.startChannelOpen();
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
}
