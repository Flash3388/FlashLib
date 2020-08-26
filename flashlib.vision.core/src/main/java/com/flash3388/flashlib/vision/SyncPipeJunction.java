package com.flash3388.flashlib.vision;

import java.util.Arrays;
import java.util.Collection;

public class SyncPipeJunction<T> implements Pipeline<T> {

    private final Collection<Pipeline<? super T>> mPipelines;

    public SyncPipeJunction(Collection<Pipeline<? super T>> pipelines) {
        mPipelines = pipelines;
    }

    @SafeVarargs
    public SyncPipeJunction(Pipeline<? super T>... pipelines) {
        this(Arrays.asList(pipelines));
    }

    @Override
    public void process(T input) throws VisionException {
        for (Pipeline<? super T> pipeline : mPipelines) {
            pipeline.process(input);
        }
    }

    @Override
    public Pipeline<T> divergeTo(Pipeline<? super T> pipeline) {
        mPipelines.add(pipeline);
        return this;
    }
}
