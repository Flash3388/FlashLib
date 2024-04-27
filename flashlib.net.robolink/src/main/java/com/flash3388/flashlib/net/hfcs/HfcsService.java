package com.flash3388.flashlib.net.hfcs;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.net.channels.messsaging.KnownMessageTypes;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.NonConnectedMessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.ReplyingNonConnectedMesssagingChannel;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.udp.UdpChannelOpener;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.concurrent.NamedThreadFactory;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.net.SocketAddress;
import java.util.function.Supplier;

public class HfcsService extends SingleUseService implements HfcsRegistry {

    private static final Logger LOGGER = Logging.getLogger("Comm", "HFCSService");
    private static final long MESSENGER_ID = 777;

    private final NamedThreadFactory mThreadFactory;
    private final ChannelUpdater mChannelUpdater;
    private final ChannelId mOurId;
    private final Clock mClock;

    private final HfcsContext mContext;
    private Supplier<MessagingChannel> mChannelSupplier;
    private MessagingChannel mChannel;
    private Thread mUpdateThread;

    public HfcsService(NamedThreadFactory threadFactory,
                       ChannelUpdater channelUpdater,
                       InstanceId ourId,
                       Clock clock) {
        mThreadFactory = threadFactory;
        mChannelUpdater = channelUpdater;
        mOurId = new ChannelId(ourId, MESSENGER_ID);
        mClock = clock;
        mContext = new HfcsContext(clock, LOGGER);
        mChannelSupplier = null;
        mChannel = null;
        mUpdateThread = null;
    }

    public void configureTargeted(SocketAddress bindAddress, SocketAddress remoteAddress) {
        mChannelSupplier = new TargetedContextSupplier(
                mOurId,
                mClock,
                mChannelUpdater,
                LOGGER,
                mContext.getMessageType(),
                bindAddress,
                remoteAddress);
    }

    public void configureReplying(SocketAddress bindAddress) {
        mChannelSupplier = new ReplierContextSupplier(
                mOurId,
                mClock,
                mChannelUpdater,
                LOGGER,
                mContext.getMessageType(),
                bindAddress);
    }

    @Override
    public void registerOutgoing(HfcsType type, Time period, Supplier<? extends Serializable> supplier) {
        mContext.updateNewOutgoing(type, period, supplier);
    }

    @Override
    public <T> HfcsRegisteredIncoming<T> registerIncoming(HfcsInType<T> type, Time receiveTimeout) {
        return mContext.updateNewIncoming(type, receiveTimeout);
    }

    @Override
    protected void startRunning() throws ServiceException {
        if (mChannelSupplier == null) {
            throw new IllegalStateException("not configured");
        }

        mContext.markNotConnected();

        mChannel = mChannelSupplier.get();
        mChannel.setListener(new MessagingChannelListener(mContext));
        mChannel.start();

        Thread updateThread = mThreadFactory.newThread("HFCS-Updater", new UpdateTask(mChannel, mContext));
        updateThread.start();
        mUpdateThread = updateThread;
    }

    @Override
    protected void stopRunning() {
        if (mUpdateThread != null) {
            mUpdateThread.interrupt();
            mUpdateThread = null;
        }

        if (mChannel != null) {
            Closeables.silentClose(mChannel);
            mChannel = null;
        }
    }

    private static class TargetedContextSupplier implements Supplier<MessagingChannel> {

        private final ChannelId mId;
        private final Clock mClock;
        private final ChannelUpdater mChannelUpdater;
        protected final Logger mLogger;
        private final MessageType mMessageType;
        private final SocketAddress mBindAddress;
        private final SocketAddress mRemoteAddress;

        private TargetedContextSupplier(ChannelId id,
                                        Clock clock,
                                        ChannelUpdater channelUpdater,
                                        Logger logger,
                                        MessageType messageType,
                                        SocketAddress bindAddress,
                                        SocketAddress remoteAddress) {
            mId = id;
            mClock = clock;
            mChannelUpdater = channelUpdater;
            mLogger = logger;
            mMessageType = messageType;
            mBindAddress = bindAddress;
            mRemoteAddress = remoteAddress;
        }

        @Override
        public MessagingChannel get() {
            KnownMessageTypes messageTypes = new KnownMessageTypes();
            messageTypes.put(mMessageType);

            NonConnectedMessagingChannel channel = new NonConnectedMessagingChannel(
                    UdpChannelOpener.targeted(mBindAddress, mRemoteAddress, mLogger),
                    mChannelUpdater,
                    messageTypes,
                    mId,
                    mClock,
                    mLogger);
            channel.enableKeepAlive();

            return channel;
        }
    }

    private static class ReplierContextSupplier implements Supplier<MessagingChannel> {

        private final ChannelId mId;
        private final Clock mClock;
        private final ChannelUpdater mChannelUpdater;
        protected final Logger mLogger;
        private final MessageType mMessageType;
        private final SocketAddress mBindAddress;

        private ReplierContextSupplier(ChannelId id,
                                       Clock clock,
                                       ChannelUpdater channelUpdater,
                                       Logger logger,
                                       MessageType messageType,
                                       SocketAddress bindAddress) {
            mId = id;
            mClock = clock;
            mChannelUpdater = channelUpdater;
            mLogger = logger;
            mMessageType = messageType;
            mBindAddress = bindAddress;
        }

        @Override
        public MessagingChannel get() {
            KnownMessageTypes messageTypes = new KnownMessageTypes();
            messageTypes.put(mMessageType);

            ReplyingNonConnectedMesssagingChannel channel = new ReplyingNonConnectedMesssagingChannel(
                    new UdpChannelOpener(mBindAddress, null, null, mLogger),
                    mChannelUpdater,
                    messageTypes,
                    mId,
                    mClock,
                    mLogger);

            return channel;
        }
    }
}
