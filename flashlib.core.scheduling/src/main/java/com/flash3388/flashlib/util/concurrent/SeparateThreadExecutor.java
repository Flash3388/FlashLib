package com.flash3388.flashlib.util.concurrent;

import com.flash3388.flashlib.time.Time;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

public class SeparateThreadExecutor implements Executor {

    private final Time mRunInterval;
    private final ThreadFactory mThreadFactory;
    private final String mName;

    public SeparateThreadExecutor(Time runInterval, ThreadFactory threadFactory) {
        mRunInterval = runInterval;
        mThreadFactory = threadFactory;
        mName = null;
    }

    public SeparateThreadExecutor(Time runInterval, String name) {
        mRunInterval = runInterval;
        mThreadFactory = null;
        mName = name;
    }

    @Override
    public void execute(Runnable command) {
        if (mThreadFactory != null) {
            Thread thread = mThreadFactory.newThread(new PeriodicTask(command, mRunInterval));
            thread.start();
        } else {
            Thread thread = new Thread(new PeriodicTask(command, mRunInterval), mName);
            thread.setDaemon(true);
            thread.start();
        }
    }
}
