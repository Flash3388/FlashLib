package com.flash3388.flashlib.net.hfcs;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.NetChannelOpener;
import com.flash3388.flashlib.net.channels.messsaging.KnownMessageTypes;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.messsaging.NonConnectedMessagingChannel;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.udp.UdpChannelOpener;
import com.flash3388.flashlib.net.messaging.ChannelId;
import com.flash3388.flashlib.net.messaging.MessageType;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.function.Supplier;

public class HfcsService extends SingleUseService implements HfcsRegistry {

    private static final Logger LOGGER = Logging.getLogger("Comm", "HFCSService");
    private static final long MESSENGER_ID = 777;

    private final ChannelUpdater mChannelUpdater;
    private final ChannelId mOurId;
    private final Clock mClock;

    private final HfcsContext mContext;
    private Supplier<MessagingChannel> mChannelSupplier;
    private MessagingChannel mChannel;
    private Thread mUpdateThread;

    public HfcsService(ChannelUpdater channelUpdater, InstanceId ourId, Clock clock) {
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

    public void configureMulticast(NetworkInterface multicastInterface,
                                   InetAddress multicastGroup,
                                   SocketAddress bindAddress,
                                   int remotePort) {
        mChannelSupplier = new MulticastContextSupplier(
                mOurId,
                mClock,
                mChannelUpdater,
                LOGGER,
                mContext.getMessageType(),
                bindAddress,
                multicastInterface,
                multicastGroup,
                remotePort);
    }

    public void configureBroadcast(SocketAddress bindAddress,
                                   int remotePort) {
        mChannelSupplier = new BroadcastContextSupplier(
                mOurId,
                mClock,
                mChannelUpdater,
                LOGGER,
                mContext.getMessageType(),
                bindAddress,
                remotePort);
    }

    @Override
    public void registerOutgoing(HfcsType type, Time period, Supplier<? extends Serializable> supplier) {
        mContext.updateNewOutgoing(type, period, supplier);
    }

    @Override
    public <T extends Serializable> HfcsRegisteredIncoming<T> registerIncoming(HfcsInType<T> type, Time receiveTimeout) {
        return mContext.updateNewIncoming(type, receiveTimeout);
    }

    @Override
    protected void startRunning() throws ServiceException {
        if (mChannelSupplier == null) {
            throw new IllegalStateException("not configured");
        }

        mContext.markedUnattached();

        mChannel = mChannelSupplier.get();
        mChannel.setListener(new MessagingChannelListener(mContext));
        mChannel.start();

        Thread updateThread = new Thread(new UpdateTask(mChannel, mContext), "HFCS-Updater");
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

    private static abstract class ContextSupplierBase implements Supplier<MessagingChannel> {

        private final ChannelId mId;
        private final Clock mClock;
        private final ChannelUpdater mChannelUpdater;
        protected final Logger mLogger;
        private final MessageType mMessageType;

        private ContextSupplierBase(ChannelId id,
                                    Clock clock,
                                    ChannelUpdater channelUpdater,
                                    Logger logger,
                                    MessageType messageType) {
            mId = id;
            mClock = clock;
            mChannelUpdater = channelUpdater;
            mLogger = logger;
            mMessageType = messageType;
        }

        @Override
        public MessagingChannel get() {
            KnownMessageTypes messageTypes = new KnownMessageTypes();
            messageTypes.put(mMessageType);

            MessagingChannel channel = new NonConnectedMessagingChannel(
                    createChannelOpener(),
                    mChannelUpdater,
                    messageTypes,
                    mId,
                    mClock,
                    mLogger);

            return channel;
        }

        protected abstract NetChannelOpener<NetChannel> createChannelOpener();
    }

    private static class TargetedContextSupplier extends ContextSupplierBase {

        private final SocketAddress mBindAddress;
        private final SocketAddress mRemoteAddress;

        private TargetedContextSupplier(ChannelId id,
                                        Clock clock,
                                        ChannelUpdater channelUpdater,
                                        Logger logger,
                                        MessageType messageType,
                                        SocketAddress bindAddress,
                                        SocketAddress remoteAddress) {
            super(id, clock, channelUpdater, logger, messageType);
            mBindAddress = bindAddress;
            mRemoteAddress = remoteAddress;
        }

        @Override
        protected NetChannelOpener<NetChannel> createChannelOpener() {
            return UdpChannelOpener.targeted(mBindAddress, mRemoteAddress, mLogger);
        }
    }

    private static class MulticastContextSupplier extends ContextSupplierBase {

        private final SocketAddress mBindAddress;
        private final NetworkInterface mMulticastInterface;
        private final InetAddress mMulticastGroup;
        private final int mRemotePort;

        private MulticastContextSupplier(ChannelId id,
                                         Clock clock,
                                         ChannelUpdater channelUpdater,
                                         Logger logger,
                                         MessageType messageType,
                                         SocketAddress bindAddress,
                                         NetworkInterface multicastInterface,
                                         InetAddress multicastGroup,
                                         int remotePort) {
            super(id, clock, channelUpdater, logger, messageType);
            mBindAddress = bindAddress;
            mMulticastInterface = multicastInterface;
            mMulticastGroup = multicastGroup;
            mRemotePort = remotePort;
        }

        @Override
        protected NetChannelOpener<NetChannel> createChannelOpener() {
            return UdpChannelOpener.multicast(mMulticastInterface, mMulticastGroup, mRemotePort, mBindAddress, mLogger);
        }
    }

    private static class BroadcastContextSupplier extends ContextSupplierBase {

        private final SocketAddress mBindAddress;
        private final int mRemotePort;

        private BroadcastContextSupplier(ChannelId id,
                                         Clock clock,
                                         ChannelUpdater channelUpdater,
                                         Logger logger,
                                         MessageType messageType,
                                         SocketAddress bindAddress,
                                         int remotePort) {
            super(id, clock, channelUpdater, logger, messageType);
            mBindAddress = bindAddress;
            mRemotePort = remotePort;
        }

        @Override
        protected NetChannelOpener<NetChannel> createChannelOpener() {
            return UdpChannelOpener.broadcast(mBindAddress, mRemotePort, mLogger);
        }
    }
}
