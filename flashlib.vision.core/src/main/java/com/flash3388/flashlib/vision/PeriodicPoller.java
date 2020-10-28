package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.time.Time;

public class PeriodicPoller<T> implements Runnable {

    private final Source<? extends T> mSource;
    private final Time mPeriod;
    private final Pipeline<? super T> mPipeline;
    private final ThrowableHandler mThrowableHandler;

    public PeriodicPoller(Source<? extends T> source,
                          Time period, Pipeline<? super T> pipeline,
                          ThrowableHandler throwableHandler) {
        mSource = source;
        mPeriod = period;
        mPipeline = pipeline;
        mThrowableHandler = throwableHandler;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                try {
                    T data = mSource.get();
                    mPipeline.process(data);
                } catch (Throwable t) {
                    mThrowableHandler.handle(t);
                }

                Thread.sleep(mPeriod.valueAsMillis());
            }
        } catch (InterruptedException e) {
        }
    }

}
