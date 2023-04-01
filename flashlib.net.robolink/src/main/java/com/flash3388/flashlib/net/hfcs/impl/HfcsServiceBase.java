package com.flash3388.flashlib.net.hfcs.impl;

import com.flash3388.flashlib.net.messaging.KnownMessageTypes;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.KnownInDataTypes;
import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.RegisteredIncoming;
import com.flash3388.flashlib.net.hfcs.Type;
import com.flash3388.flashlib.net.hfcs.messages.HfcsMessageType;
import com.flash3388.flashlib.net.util.NetServiceBase;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.function.Supplier;

public abstract class HfcsServiceBase extends NetServiceBase implements HfcsRegistry {

    protected static final Logger LOGGER = Logging.getLogger("Comm", "HFCSService");

    protected final KnownInDataTypes mInDataTypes;
    protected final EventController mEventController;
    protected final BlockingQueue<OutDataNode> mOutDataQueue;
    protected final Map<InType<?>, InDataNode> mInDataNodes;

    public HfcsServiceBase(InstanceId ourId, Clock clock) {
        super(ourId, clock);

        mInDataTypes = new KnownInDataTypes();

        mEventController = Controllers.newSyncExecutionController();
        mOutDataQueue = new DelayQueue<>();
        mInDataNodes = new ConcurrentHashMap<>();
    }

    @Override
    public void registerOutgoing(Type type, Time period, Supplier<? extends OutData> supplier) {
        mOutDataQueue.add(new OutDataNode(mClock, type, supplier, period));
    }

    @Override
    public <T> RegisteredIncoming<T> registerIncoming(InType<T> type, Time receiveTimeout) {
        mInDataTypes.put(type);
        mInDataNodes.put(type, new InDataNode(type, receiveTimeout, LOGGER));
        return new RegisteredIncomingImpl<>(mEventController, type);
    }

    protected KnownMessageTypes getMessageTypes() {
        KnownMessageTypes messageTypes = new KnownMessageTypes();
        messageTypes.put(new HfcsMessageType(mInDataTypes));
        return messageTypes;
    }
}
