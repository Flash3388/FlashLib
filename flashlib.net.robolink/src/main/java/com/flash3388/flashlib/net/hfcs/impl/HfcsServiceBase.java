package com.flash3388.flashlib.net.hfcs.impl;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.InType;
import com.flash3388.flashlib.net.hfcs.KnownInDataTypes;
import com.flash3388.flashlib.net.hfcs.OutData;
import com.flash3388.flashlib.net.hfcs.RegisteredIncoming;
import com.flash3388.flashlib.net.hfcs.Type;
import com.flash3388.flashlib.net.hfcs.messages.DataMessageType;
import com.flash3388.flashlib.net.hfcs.messages.InPackage;
import com.flash3388.flashlib.net.message.KnownMessageTypes;
import com.flash3388.flashlib.net.message.MessageReader;
import com.flash3388.flashlib.net.message.MessageWriter;
import com.flash3388.flashlib.net.message.v1.MessageReaderImpl;
import com.flash3388.flashlib.net.message.v1.MessageWriterImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.unique.InstanceId;
import com.notifier.Controllers;
import com.notifier.EventController;
import org.slf4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.function.Supplier;

public abstract class HfcsServiceBase extends SingleUseService implements HfcsRegistry {

    protected static final Logger LOGGER = Logging.getLogger("Comm", "HFCSService");

    protected final InstanceId mOurId;
    protected final Clock mClock;

    protected final KnownInDataTypes mInDataTypes;
    protected final EventController mEventController;
    protected final BlockingQueue<OutDataNode> mOutDataQueue;
    protected final MessageWriter mMessageWriter;
    protected final MessageReader mMessageReader;

    private final List<Thread> mThreads;

    public HfcsServiceBase(InstanceId ourId, Clock clock) {
        mOurId = ourId;
        mClock = clock;

        mInDataTypes = new KnownInDataTypes();

        KnownMessageTypes messageTypes = new KnownMessageTypes();
        messageTypes.put(new DataMessageType(new InPackage(mInDataTypes)));

        mMessageWriter = new MessageWriterImpl(ourId);
        mMessageReader = new MessageReaderImpl(ourId, messageTypes);

        mEventController = Controllers.newSyncExecutionController();
        mOutDataQueue = new DelayQueue<>();

        mThreads = new LinkedList<>();
    }

    @Override
    public void registerOutgoing(Type type, Time period, Supplier<? extends OutData> supplier) {
        mOutDataQueue.add(new OutDataNode(mClock, type, supplier, period));
    }

    @Override
    public <T> RegisteredIncoming<T> registerIncoming(InType<T> type) {
        mInDataTypes.put(type);
        return new RegisteredIncomingImpl<>(mEventController, type);
    }

    @Override
    protected void startRunning() throws ServiceException {
        List<Thread> threads = new LinkedList<>();

        Map<String, Runnable> tasks = createTasks();
        for (Map.Entry<String, Runnable> entry : tasks.entrySet()) {
            Thread thread = new Thread(entry.getValue(), entry.getKey());
            thread.setDaemon(true);
            threads.add(thread);
        }

        mThreads.clear();
        mThreads.addAll(threads);

        for (Thread thread : threads) {
            thread.start();
        }
    }

    @Override
    protected void stopRunning() {
        for (Thread thread : mThreads) {
            thread.interrupt();
        }

        mThreads.clear();

        freeResources();
    }

    protected abstract Map<String, Runnable> createTasks();
    protected abstract void freeResources();
}
