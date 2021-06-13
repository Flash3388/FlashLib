package com.flash3388.flashlib.scheduling.mt;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class MtExecutorServiceWorkers implements MtWorkers {

    private final ExecutorService mExecutorService;
    private final int mWorkerCount;
    private final Set<Future<?>> mFutures;

    public MtExecutorServiceWorkers(ExecutorService executorService, int workerCount) {
        mExecutorService = executorService;
        mWorkerCount = workerCount;

        mFutures = new HashSet<>();
    }

    @Override
    public void runWorkers(Supplier<Runnable> taskSupplier) {
        for (int i = 0; i < mWorkerCount; i++) {
            Runnable runnable = taskSupplier.get();
            Future<?> future = mExecutorService.submit(runnable);
            mFutures.add(future);
        }
    }

    @Override
    public void stopWorkers() {
        for (Future<?> future : mFutures) {
            future.cancel(true);
        }

        mFutures.clear();
    }
}
