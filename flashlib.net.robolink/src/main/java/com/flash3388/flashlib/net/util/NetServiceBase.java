package com.flash3388.flashlib.net.util;

import com.castle.concurrent.service.SingleUseService;
import com.castle.exceptions.ServiceException;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class NetServiceBase extends SingleUseService {

    protected final InstanceId mOurId;
    protected final Clock mClock;

    private final List<Thread> mThreads;

    public NetServiceBase(InstanceId ourId, Clock clock) {
        mOurId = ourId;
        mClock = clock;

        mThreads = new LinkedList<>();
    }

    @Override
    protected final void startRunning() throws ServiceException {
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
    protected final void stopRunning() {
        for (Thread thread : mThreads) {
            thread.interrupt();
        }

        mThreads.clear();

        freeResources();
    }

    protected abstract Map<String, Runnable> createTasks();
    protected abstract void freeResources();
}
