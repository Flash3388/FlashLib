package com.flash3388.flashlib.vision;

import java.util.Arrays;
import java.util.Collection;

@FunctionalInterface
public interface Pipeline<T> {

    void process(T input) throws VisionException;

    default Pipeline<T> divergeTo(Pipeline<? super T> pipeline) {
        return new SyncDivergingPipeline<>(Arrays.asList(this, pipeline));
    }

    class SyncDivergingPipeline<T> implements Pipeline<T> {

        private final Collection<Pipeline<? super T>> mPipelines;

        private SyncDivergingPipeline(Collection<Pipeline<? super T>> pipelines) {
            mPipelines = pipelines;
        }

        @Override
        public void process(T input) throws VisionException {
            for (Pipeline<? super T> pipeline : mPipelines) {
                pipeline.process(input);
            }
        }
    }
}
