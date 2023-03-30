package com.flash3388.flashlib.util;

import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class FlashLibMainThreadImpl implements FlashLibMainThread {

    private static final Logger LOGGER = Logging.getMainLogger();

    private final Thread mThread;
    private final Queue<Runnable> mTasks;

    public FlashLibMainThreadImpl() {
        mThread = Thread.currentThread();
        mTasks = new ConcurrentLinkedDeque<>();
    }

    @Override
    public boolean isCurrentThread() {
        Thread thread = Thread.currentThread();
        return mThread.getId() == thread.getId();
    }

    @Override
    public void verifyCurrentThread() {
        if (!isCurrentThread()) {
            Thread thread = Thread.currentThread();
            LOGGER.error("Current thread {} is not main thread {}", thread.getId(), mThread.getId());
            throw new IllegalStateException("current thread is not main thread");
        }
    }

    @Override
    public void runOnThisThread(Runnable runnable) {
        if (isCurrentThread()) {
            LOGGER.debug("Already current thread; Executing task {} for thread {}", runnable, mThread.getId());
            runnable.run();
        } else {
            LOGGER.debug("Adding task {} for thread {}", runnable, mThread.getId());
            mTasks.add(runnable);
        }
    }

    @Override
    public synchronized void executePendingTasks() {
        verifyCurrentThread();

        int size = mTasks.size();
        while (size-- > 0) {
            Runnable runnable = mTasks.remove();
            LOGGER.debug("Executing task {} on thread {}", runnable, mThread.getId());
            runnable.run();
        }
    }
}
