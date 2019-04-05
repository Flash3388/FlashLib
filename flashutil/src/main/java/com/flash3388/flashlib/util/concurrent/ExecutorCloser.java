package com.flash3388.flashlib.util.concurrent;

import com.flash3388.flashlib.time.Time;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;

public class ExecutorCloser implements Closeable {

    private static final long DEFAULT_SHUTDOWN_WAIT_MINUTES = 1;

    private final ExecutorService mExecutorService;
    private final Time mShutdownWait;

    public ExecutorCloser(ExecutorService executorService, Time shutdownWait) {
        mExecutorService = executorService;
        mShutdownWait = shutdownWait;
    }

    public ExecutorCloser(ExecutorService executorService) {
        this(executorService, Time.minutes(DEFAULT_SHUTDOWN_WAIT_MINUTES));
    }

    @Override
    public void close() {
        mExecutorService.shutdownNow();

        try {
            while (!mExecutorService.awaitTermination(mShutdownWait.getValue(), mShutdownWait.getUnit()));
        } catch (InterruptedException e) {
            Interrupts.preserveInterruptState();
        }
    }
}
