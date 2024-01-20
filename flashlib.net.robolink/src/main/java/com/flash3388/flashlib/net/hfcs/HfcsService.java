package com.flash3388.flashlib.net.hfcs;

import com.flash3388.flashlib.io.Serializable;
import com.flash3388.flashlib.net.channels.NetChannelConnector;
import com.flash3388.flashlib.net.channels.messsaging.BasicMessagingChannelImpl;
import com.flash3388.flashlib.net.channels.messsaging.KnownMessageTypes;
import com.flash3388.flashlib.net.channels.messsaging.MessagingChannel;
import com.flash3388.flashlib.net.channels.udp.UdpConnector;
import com.flash3388.flashlib.net.messaging.Message;
import com.flash3388.flashlib.net.messaging.ServerClock;
import com.flash3388.flashlib.net.util.NetServiceBase;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.io.Closeable;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.function.Supplier;

public class HfcsService extends NetServiceBase implements HfcsRegistry {

    private static final Logger LOGGER = Logging.getLogger("Comm", "HFCSService");

    private final InstanceId mOurId;
    private final Clock mClock;

    private final KnownInDataTypes mInDataTypes;
    private final EventController mEventController;
    private final BlockingQueue<OutDataNode> mOutDataQueue;
    private final Map<HfcsInType<?>, InDataNode> mInDataNodes;

    public HfcsService(InstanceId ourId, Clock clock) {
        mOurId = ourId;
        mClock = clock;

        mInDataTypes = new KnownInDataTypes();
        mEventController = Controllers.newSyncExecutionController();
        mOutDataQueue = new DelayQueue<>();
        mInDataNodes = new ConcurrentHashMap<>();
    }

    public void configureTargeted(SocketAddress remoteAddress) {

    }

    public void configureMulticast() {

    }

    public void configureBroadcast() {

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


        Map<String, Runnable> tasks = new HashMap<>();
        tasks.put("MessengerService-ReadTask", mContext.readTask);

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
        private final Logger mLogger;
        private final EventController mEventController;
        private final KnownMessageTypes mKnownMessageTypes;
        private final BlockingQueue<Message> mWriteQueue;

        private ContextSupplierBase(InstanceId instanceId,
                                    Clock clock,
                                    SocketAddress serverAddress,
                                      Logger logger,
                                      EventController eventController,
                                      KnownMessageTypes knownMessageTypes,
                                      BlockingQueue<Message> writeQueue) {
            mInstanceId = instanceId;
            mClock = clock;
            mServerAddress = serverAddress;
            mLogger = logger;
            mEventController = eventController;
            mKnownMessageTypes = knownMessageTypes;
            mWriteQueue = writeQueue;
        }

        @Override
        public Context get() {
            MessagingChannel channel = new BasicMessagingChannelImpl(
                    new UdpConnector(),
                    mServerAddress,
                    mInstanceId,
                    clock,
                    mLogger,
                    mKnownMessageTypes);

            return new Context(channel, readTask, writeTask);
        }

        protected abstract MessagingChannel createChannel();
    }
}
