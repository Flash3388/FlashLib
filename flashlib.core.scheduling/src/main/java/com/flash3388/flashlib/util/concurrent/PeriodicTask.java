package com.flash3388.flashlib.util.concurrent;

import com.flash3388.flashlib.time.Time;

public class PeriodicTask implements Runnable {

    private final Runnable mTask;
    private final Time mRunInterval;

    public PeriodicTask(Runnable task, Time runInterval) {
        mTask = task;
        mRunInterval = runInterval;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            mTask.run();

            try {
                Thread.sleep(mRunInterval.valueAsMillis());
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
