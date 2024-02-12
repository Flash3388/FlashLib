package com.flash3388.flashlib.net.channels.messsaging;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
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
import java.nio.channels.SelectionKey;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MessagingChannelBase implements MessagingChannel {

    protected static class ChannelData {
        public final NetChannel channel;
        public final UpdateRegistration registration;
        public boolean readyForWriting;

        protected ChannelData(NetChannel channel, UpdateRegistration registration) {
            this.channel = channel;
            this.registration = registration;
            this.readyForWriting = false;
        }
    }

    protected static class SendRequest {
        public final Message message;
        public final boolean isOnlyForServer;

        protected SendRequest(Message message, boolean isOnlyForServer) {
            this.message = message;
            this.isOnlyForServer = isOnlyForServer;
        }
    }

    protected static class ReceivedMessage {
        public final MessageHeader header;
        public final Message message;

        public ReceivedMessage(MessageHeader header, Message message) {
            this.header = header;
            this.message = message;
        }
    }

    private static final Time OPEN_DELAY = Time.seconds(1);

    private final NetChannelOpener<? extends NetChannel> mChannelOpener;
    private final ChannelUpdater mChannelUpdater;
    private final ChannelId mOurId;
    protected final Logger mLogger;

    private final AtomicReference<ChannelData> mChannel;
    private final AtomicReference<Listener> mListener;
    private final Queue<SendRequest> mQueue;
    private final ChannelListener mChannelListener;
    private final ChannelOpenListener<NetChannel> mOpenListener;
    private boolean mStarted;
    private boolean mClosed;

    protected MessagingChannelBase(NetChannelOpener<? extends NetChannel> channelOpener,
                                   ChannelUpdater channelUpdater,
                                   KnownMessageTypes messageTypes,
                                   ChannelId ourId,
                                   Clock clock,
                                   Logger logger) {
        mChannelOpener = channelOpener;
        mChannelUpdater = channelUpdater;
        mOurId = ourId;
        mLogger = logger;

        mChannel = new AtomicReference<>();
        mListener = new AtomicReference<>();
        mQueue = new ArrayDeque<>();
        mChannelListener = new ChannelListenerImpl(this, messageTypes, ourId, clock, logger);
        mOpenListener = new OpenListenerImpl(this, logger);
        mStarted = false;
        mClosed = false;
    }

    @Override
    public final void start() {
        verifyNotClosed();
        verifyNotStarted();

        startChannelOpen(false);
        mStarted = true;
    }

    @Override
    public final void queue(Message message) {
        verifyNotClosed();
        if (!mStarted) {
            throw new IllegalStateException("not started");
        }

        queue(message, false);
    }

    @Override
    public final void setListener(Listener listener) {
        verifyNotClosed();
        verifyNotStarted();

        mListener.set(listener);
    }

    @Override
    public final void resetChannel() {
        verifyNotClosed();
        if (!mStarted) {
            throw new IllegalStateException("not started");
        }

        ChannelData channelData = mChannel.get();
        if (channelData == null) {
            // no need to reset, as channel not yet opened
            return;
        }

        Closeables.silentClose(this::closeChannel);
        startChannelOpen(true);
    }

    @Override
    public final void close() throws IOException {
        if (mClosed) {
            return;
        }

        mClosed = true;
        mStarted = false;
        closeChannel();
    }

    protected final void queue(Message message, boolean onlyForServer) {
        mLogger.debug("Queuing new message");

        synchronized (mQueue) {
            ChannelData channel = mChannel.get();
            mQueue.add(new SendRequest(message, onlyForServer));

            if (channel != null && channel.readyForWriting) {
                channel.registration.requestReadWriteUpdates();
            }
        }
    }

    protected final void markReadyForWriting() {
        ChannelData channel = mChannel.get();
        if (channel == null) {
            return;
        }

        synchronized (mQueue) {
            channel.readyForWriting = true;
            channel.registration.requestReadWriteUpdates();
        }
    }

    protected final void markNotReadyForWriting() {
        ChannelData channel = mChannel.get();
        if (channel == null) {
            return;
        }

        synchronized (mQueue) {
            channel.readyForWriting = false;
            channel.registration.requestReadUpdates();
        }
    }

    protected final void reportToUserAsConnected() {
        Listener listener = mListener.get();
        if (listener != null) {
            try {
                listener.onConnect();
            } catch (Throwable ignore) {}
        }
    }

    protected final void reportToUserAsDisconnected() {
        Listener listener = mListener.get();
        if (listener != null) {
            try {
                listener.onDisconnect();
            } catch (Throwable ignore) {}
        }
    }

    protected final ChannelData getChannel() {
        return mChannel.get();
    }

    protected final ChannelId getOurId() {
        return mOurId;
    }

    protected final void verifyNotStarted() {
        if (mStarted) {
            throw new IllegalStateException("channel started");
        }
    }

    protected final void verifyNotClosed() {
        if (mClosed) {
            throw new IllegalStateException("closed");
        }
    }

    private void startChannelOpen(boolean delayed) {
        mLogger.debug("Requesting open channel");
        mChannelUpdater.requestOpenChannel(mChannelOpener, mOpenListener, mChannelListener, delayed ? OPEN_DELAY : Time.seconds(0));
    }

    private void onChannelOpen(NetChannel channel, UpdateRegistration registration) {
        mLogger.debug("Channel opened");

        ChannelData channelWrapper = implCreateChannelWrapper(channel, registration);
        mChannel.set(channelWrapper);

        implDoOnChannelOpen(channelWrapper);
    }

    private void onNewMessage(MessageHeader header, Message message) {
        mLogger.debug("New message received: sender={}, type={}",
                header.getSender(),
                header.getMessageType());

        ChannelData channel = mChannel.get();
        if (channel == null) {
            mLogger.warn("new message while channel is null");
            return;
        }

        Optional<ReceivedMessage> processed = implDoOnNewMessage(channel, header, message);
        if (!processed.isPresent()) {
            return;
        }

        Listener listener = mListener.get();
        if (listener != null) {
            try {
                listener.onNewMessage(header, message);
            } catch (Throwable ignore) {}
        }
    }

    private void closeChannel() {
        ChannelData data = mChannel.getAndSet(null);
        if (data == null) {
            return;
        }

        try {
            data.registration.cancel();
        } catch (Throwable ignore) {}

        Closeables.silentClose(data.channel);

        implDoOnChannelClose();
    }

    protected abstract ChannelData implCreateChannelWrapper(NetChannel channel, UpdateRegistration registration);

    protected abstract void implDoOnChannelOpen(ChannelData channel);
    protected abstract Optional<ReceivedMessage> implDoOnNewMessage(ChannelData channel, MessageHeader header, Message message);
    protected abstract void implDoOnChannelConnectable();
    protected abstract void implDoOnChannelUpdate(Object param);
    protected abstract void implDoOnChannelClose();

    private static class ChannelListenerImpl implements ChannelListener {

        private final WeakReference<MessagingChannelBase> mChannel;
        private final Clock mClock;
        private final Logger mLogger;

        private final MessageReadingContext mReadingContext;
        private final MessageSerializer mSerializer;
        private final List<SendRequest> mWriteQueueLocal;

        private ChannelListenerImpl(MessagingChannelBase channel,
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

        }

        @Override
        public void onConnectable(SelectionKey key) {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.implDoOnChannelConnectable();
        }

        @Override
        public void onReadable(SelectionKey key) {
            MessagingChannelBase ourChannel = mChannel.get();
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
                ourChannel.resetChannel();
            }
        }

        @Override
        public void onWritable(SelectionKey key) {
            MessagingChannelBase ourChannel = mChannel.get();
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
                    ourChannel.resetChannel();
                    break;
                }
            }
        }

        @Override
        public void onRequestedUpdate(SelectionKey key, Object param) {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.implDoOnChannelUpdate(param);
        }

        private void parseMessages(MessagingChannelBase ourChannel) throws IOException {
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

    private static class OpenListenerImpl implements ChannelOpenListener<NetChannel> {

        private final WeakReference<MessagingChannelBase> mChannel;
        private final Logger mLogger;

        private OpenListenerImpl(MessagingChannelBase channel, Logger logger) {
            mChannel = new WeakReference<>(channel);
            mLogger = logger;
        }

        @Override
        public void onOpen(NetChannel channel, UpdateRegistration registration) {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.onChannelOpen(channel, registration);
        }

        @Override
        public void onError(Throwable t) {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.startChannelOpen(true);
        }
    }
}
