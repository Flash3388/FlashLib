package com.flash3388.flashlib.net.channels.messsaging;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.NetAddress;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
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
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReference;

public abstract class MessagingChannelBase implements MessagingChannel {

    private static final Time OPEN_DELAY = Time.seconds(1);

    private final NetChannelOpener<? extends NetChannel> mChannelOpener;
    private final ChannelUpdater mChannelUpdater;
    private final ChannelId mOurId;
    protected final Logger mLogger;

    private final AtomicReference<ChannelData> mChannel;
    private final AtomicReference<Listener> mListener;
    private final Queue<SendRequest> mQueue;
    private final BaseChannelListener mChannelListener;
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
        mChannelListener = new BaseChannelListener(
                new ChannelControllerImpl(this, logger),
                messageTypes,
                ourId,
                clock,
                logger,
                channelOpener.isTargetChannelStreaming());
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
        mChannelUpdater.requestOpenChannel(
                mChannelOpener,
                mOpenListener,
                mChannelListener,
                delayed ? OPEN_DELAY : Time.seconds(0));
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

        mChannelListener.reset();

        implDoOnChannelClose();
    }

    protected abstract void implDoOnChannelOpen(ChannelData channel);
    protected abstract Optional<ReceivedMessage> implDoOnNewMessage(ChannelData channel, NetAddress sender, MessageHeader header, Message message);
    protected abstract void implDoOnChannelConnectable();
    protected abstract void implDoOnChannelUpdate(Object param);
    protected abstract void implDoOnChannelClose();

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

            mLogger.debug("Channel opened");

            ChannelData channelWrapper = new ChannelData(channel, registration);
            ourChannel.mChannel.set(channelWrapper);

            ourChannel.implDoOnChannelOpen(channelWrapper);
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

    private static class ChannelControllerImpl implements ChannelController {

        private final WeakReference<MessagingChannelBase> mChannel;
        private final Logger mLogger;

        private ChannelControllerImpl(MessagingChannelBase channel, Logger logger) {
            mChannel = new WeakReference<>(channel);
            mLogger = logger;
        }

        @Override
        public NetChannel getChannel() {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return null;
            }


            ChannelData channel = ourChannel.mChannel.get();
            if (channel == null) {
                mLogger.warn("Channel is closed but received new read update");
                return null;
            }

            return channel.channel;
        }

        @Override
        public SendRequest getNextSendRequest() {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return null;
            }


            ChannelData channel = ourChannel.mChannel.get();
            if (channel == null) {
                mLogger.warn("Channel is closed but received new read update");
                return null;
            }

            SendRequest request;
            synchronized (ourChannel.mQueue) {
                request = ourChannel.mQueue.poll();
                if (request == null) {
                    channel.registration.requestReadUpdates();
                    return null;
                }
            }

            return request;
        }

        @Override
        public void resetChannel() {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.resetChannel();
        }

        @Override
        public void onNewMessage(NetAddress address, MessageHeader header, Message message) {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            mLogger.debug("New message received: sender={}, type={}",
                    header.getSender(),
                    header.getMessageType());

            ChannelData channel = ourChannel.mChannel.get();
            if (channel == null) {
                mLogger.warn("new message while channel is null");
                return;
            }

            Optional<ReceivedMessage> processed = ourChannel.implDoOnNewMessage(channel, address, header, message);
            if (!processed.isPresent()) {
                return;
            }

            Listener listener = ourChannel.mListener.get();
            if (listener != null) {
                try {
                    listener.onNewMessage(header, message);
                } catch (Throwable ignore) {}
            }
        }

        @Override
        public void onMessageSendingFailed(Message message, Throwable cause) {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            Listener listener = ourChannel.mListener.get();
            if (listener != null) {
                try {
                    listener.onMessageSendingFailed(message, cause);
                } catch (Throwable ignore) {}
            }
        }

        @Override
        public void onChannelCustomUpdate(Object param) {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.implDoOnChannelUpdate(param);
        }

        @Override
        public void onChannelConnectable() {
            MessagingChannelBase ourChannel = mChannel.get();
            if (ourChannel == null) {
                mLogger.warn("MessagingChannel was garbage collected");
                return;
            }

            ourChannel.implDoOnChannelConnectable();
        }
    }
}
