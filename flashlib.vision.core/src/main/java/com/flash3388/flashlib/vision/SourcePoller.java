package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;

public class SourcePoller<T> implements Runnable {

    private final Source<? extends T> mSource;
    private final Pipeline<? super T> mPipeline;
    private final ThrowableHandler mThrowableHandler;

    public SourcePoller(Source<? extends T> source,
                        Pipeline<? super T> pipeline,
                        ThrowableHandler throwableHandler) {
        mSource = source;
        mPipeline = pipeline;
        mThrowableHandler = throwableHandler;
    }

    @Override
    public void run() {
        try {
            T data = mSource.get();
            mPipeline.process(data);
        } catch (Throwable t) {
            mThrowableHandler.handle(t);
        }
    }
}
