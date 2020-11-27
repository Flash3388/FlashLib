package com.flash3388.flashlib.util.concurrent;

import com.flash3388.flashlib.time.Time;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class ServiceIntervalExecutor implements Executor {

    private final ExecutorService mExecutorService;
    private final Time mRunInterval;

    public ServiceIntervalExecutor(ExecutorService executorService, Time runInterval) {
        mExecutorService = executorService;
        mRunInterval = runInterval;
    }

    @Override
    public void execute(Runnable command) {
        mExecutorService.execute(new PeriodicTask(command, mRunInterval));
    }
}
