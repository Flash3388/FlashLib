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
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
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

    private final KnownInDataTypes mInDataTypes;
    private final HfcsMessageType mMessageType;
    private final EventController mEventController;
    private final BlockingQueue<OutDataNode> mOutDataQueue;
    private final Map<HfcsInType<?>, InDataNode> mInDataNodes;

    private Supplier<MessagingChannel> mContextSupplier;
    private MessagingChannel mChannel;

    public HfcsService(ChannelUpdater channelUpdater, InstanceId ourId, Clock clock) {
        mChannelUpdater = channelUpdater;
        mOurId = new ChannelId(ourId, MESSENGER_ID);
        mClock = clock;

        mInDataTypes = new KnownInDataTypes();
        mMessageType = new HfcsMessageType(mInDataTypes, LOGGER);
        mEventController = Controllers.newSyncExecutionController();
        mOutDataQueue = new DelayQueue<>();
        mInDataNodes = new ConcurrentHashMap<>();
    }

    public void configureTargeted(SocketAddress bindAddress, SocketAddress remoteAddress) {
        mContextSupplier = new TargetedContextSupplier(
                mOurId,
                mClock,
                mChannelUpdater,
                LOGGER,
                mMessageType,
                bindAddress,
                remoteAddress);
    }

    public void configureMulticast(NetworkInterface multicastInterface,
                                   InetAddress multicastGroup,
                                   SocketAddress bindAddress,
                                   int remotePort) {
        mContextSupplier = new MulticastContextSupplier(
                mOurId,
                mClock,
                mChannelUpdater,
                LOGGER,
                mMessageType,
                bindAddress,
                multicastInterface,
                multicastGroup,
                remotePort);
    }

    public void configureBroadcast(SocketAddress bindAddress,
                                   int remotePort) {
        mContextSupplier = new BroadcastContextSupplier(
                mOurId,
                mClock,
                mChannelUpdater,
                LOGGER,
                mMessageType,
                bindAddress,
                remotePort);
    }

    @Override
    public void registerOutgoing(HfcsType type, Time period, Supplier<? extends Serializable> supplier) {
        mOutDataQueue.add(new OutDataNode(mClock, type, supplier, period));
    }

    @Override
    public <T> HfcsRegisteredIncoming<T> registerIncoming(HfcsInType<T> type, Time receiveTimeout) {
        mInDataTypes.put(type);
        mInDataNodes.put(type, new InDataNode(type, receiveTimeout, LOGGER));
        return new RegisteredIncomingImpl<>(mEventController, type);
    }

    @Override
    protected void startRunning() throws ServiceException {
        if (mContextSupplier == null) {
            throw new IllegalStateException("not configured");
        }

        mChannel = mContextSupplier.get();
        mChannel.setListener();
        mChannel.start();

        Thread updateThread = new Thread()
    }

    @Override
    protected void stopRunning() {
        Closeables.silentClose(mChannel);
    }

    private static abstract class ContextSupplierBase implements Supplier<MessagingChannel> {

        private final ChannelId mId;
        private final Clock mClock;
        private final ChannelUpdater mChannelUpdater;
        protected final Logger mLogger;
        private final HfcsMessageType mMessageType;

        private ContextSupplierBase(ChannelId id,
                                    Clock clock,
                                    ChannelUpdater channelUpdater,
                                    Logger logger,
                                    HfcsMessageType messageType) {
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
                                        HfcsMessageType messageType,
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
                                         HfcsMessageType messageType,
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
                                         HfcsMessageType messageType,
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
