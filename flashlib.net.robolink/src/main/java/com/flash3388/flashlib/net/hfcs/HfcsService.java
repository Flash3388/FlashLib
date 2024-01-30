package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

public class HfcsService /*extends NetServiceBase implements HfcsRegistry*/ {

    private static final Logger LOGGER = Logging.getLogger("Comm", "HFCSService");

   /* private final InstanceId mOurId;
    private final Clock mClock;

    private final KnownInDataTypes mInDataTypes;
    private final EventController mEventController;
    private final BlockingQueue<OutDataNode> mOutDataQueue;
    private final Map<HfcsInType<?>, InDataNode> mInDataNodes;

    private Supplier<Context> mContextSupplier;
    private Context mContext;

    public HfcsService(InstanceId ourId, Clock clock) {
        mOurId = ourId;
        mClock = clock;

        mInDataTypes = new KnownInDataTypes();
        mEventController = Controllers.newSyncExecutionController();
        mOutDataQueue = new DelayQueue<>();
        mInDataNodes = new ConcurrentHashMap<>();
    }

    public void configureTargeted(SocketAddress bindAddress, SocketAddress remoteAddress) {
        mContextSupplier = new SimpleContextSupplier(
                mOurId,
                mClock,
                remoteAddress,
                LOGGER,
                mEventController,
                bindAddress);
    }

    public void configureMulticast(NetworkInterface multicastInterface,
                                   InetAddress multicastGroup,
                                   SocketAddress bindAddress,
                                   int remotePort) {
        mContextSupplier = new MulticastContextSupplier(
                mOurId,
                mClock,
                new InetSocketAddress(multicastGroup, remotePort),
                LOGGER,
                mEventController,
                bindAddress,
                multicastInterface,
                multicastGroup);
    }

    public void configureBroadcast(SocketAddress bindAddress,
                                   int remotePort) {
        try {
            InetAddress address = InetAddress.getByName("255.255.255.255");

            mContextSupplier = new BroadcastContextSupplier(
                    mOurId,
                    mClock,
                    new InetSocketAddress(address, remotePort),
                    LOGGER,
                    mEventController,
                    bindAddress);
        } catch (UnknownHostException e) {
            throw new Error("broadcast address not found", e);
        }
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
    protected Map<String, Runnable> createTasks() {
        if (mContextSupplier == null) {
            throw new IllegalStateException("not configured");
        }

        mContext = mContextSupplier.get();

        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("HfcsService.UpdateTask", mContext.updateTask);

        return tasks;
    }

    @Override
    protected void freeResources() {

    }

    private static class Context {

        public final Closeable channel;
        public final Runnable updateTask;

        public Context(Closeable channel, Runnable updateTask) {
            this.channel = channel;
            this.updateTask = updateTask;
        }
    }

    private static abstract class ContextSupplierBase implements Supplier<Context> {

        private final InstanceId mInstanceId;
        private final Clock mClock;
        private final SocketAddress mServerAddress;
        protected final Logger mLogger;
        private final EventController mEventController;

        private ContextSupplierBase(InstanceId instanceId,
                                    Clock clock,
                                    SocketAddress serverAddress,
                                    Logger logger,
                                    EventController eventController) {
            mInstanceId = instanceId;
            mClock = clock;
            mServerAddress = serverAddress;
            mLogger = logger;
            mEventController = eventController;
        }

        @Override
        public Context get() {
            KnownMessageTypes messageTypes = new KnownMessageTypes();
            MessagingChannel channel = new BasicMessagingChannelImpl(
                    createConnector(),
                    mServerAddress,
                    mInstanceId,
                    mClock,
                    mLogger,
                    messageTypes);

            Runnable task = new UpdateTask(channel);
            return new Context(channel, task);
        }

        protected abstract UdpConnector createConnector();
    }

    private static class SimpleContextSupplier extends ContextSupplierBase {

        private final SocketAddress mBindAddress;

        private SimpleContextSupplier(InstanceId instanceId,
                                      Clock clock,
                                      SocketAddress serverAddress,
                                      Logger logger,
                                      EventController eventController,
                                      SocketAddress bindAddress) {
            super(instanceId, clock, serverAddress, logger, eventController);
            mBindAddress = bindAddress;
        }

        @Override
        protected UdpConnector createConnector() {
            return new UdpConnector(mBindAddress, mLogger);
        }
    }

    private static class MulticastContextSupplier extends ContextSupplierBase {

        private final SocketAddress mBindAddress;
        private final NetworkInterface mMulticastInterface;
        private final InetAddress mMulticastGroup;

        private MulticastContextSupplier(InstanceId instanceId,
                                         Clock clock,
                                         SocketAddress serverAddress,
                                         Logger logger,
                                         EventController eventController,
                                         SocketAddress bindAddress,
                                         NetworkInterface multicastInterface,
                                         InetAddress multicastGroup) {
            super(instanceId, clock, serverAddress, logger, eventController);
            mBindAddress = bindAddress;
            mMulticastInterface = multicastInterface;
            mMulticastGroup = multicastGroup;
        }

        @Override
        protected UdpConnector createConnector() {
            return UdpConnector.multicast(mMulticastInterface, mMulticastGroup, mBindAddress, mLogger);
        }
    }

    private static class BroadcastContextSupplier extends ContextSupplierBase {

        private final SocketAddress mBindAddress;

        private BroadcastContextSupplier(InstanceId instanceId,
                                         Clock clock,
                                         SocketAddress serverAddress,
                                         Logger logger,
                                         EventController eventController,
                                         SocketAddress bindAddress) {
            super(instanceId, clock, serverAddress, logger, eventController);
            mBindAddress = bindAddress;
        }

        @Override
        protected UdpConnector createConnector() {
            return UdpConnector.broadcast(mBindAddress, mLogger);
        }
    }*/
}
