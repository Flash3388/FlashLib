package com.flash3388.flashlib.scheduling.threading;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class MtDaemonThreadWorkers implements MtWorkers {

    private final Function<Runnable, Thread> mWorkerFactory;
    private final int mWorkerCount;
    private final Set<Thread> mThreads;

    public MtDaemonThreadWorkers(Function<Runnable, Thread> workerFactory, int workerCount) {
        mWorkerFactory = workerFactory;
        mWorkerCount = workerCount;
        mThreads = new HashSet<>();
    }

    public MtDaemonThreadWorkers(ThreadGroup group, String threadName, int workerCount) {
        this((task)-> new Thread(group, task, threadName), workerCount);
    }

    public MtDaemonThreadWorkers(int workerCount) {
        this(new ThreadGroup("mt-scheduler"), "mt-scheduler-worker", workerCount);
    }

    @Override
    public void runWorkers(Supplier<Runnable> taskSupplier) {
        for (int i = 0; i < mWorkerCount; i++) {
            Runnable task = taskSupplier.get();

            Thread thread = mWorkerFactory.apply(task);
            thread.setDaemon(true);
            mThreads.add(thread);

            thread.start();
        }
    }

    @Override
    public void stopWorkers() {
        for (Thread thread : mThreads) {
            thread.interrupt();
        }

        mThreads.clear();
    }
}
