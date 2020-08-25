package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.vision.processing.ProcessingException;

import java.util.Arrays;
import java.util.Collection;

@FunctionalInterface
public interface Pipeline<T> {

    void process(T input) throws ProcessingException;

    default Pipeline<T> divergeTo(Pipeline<? super T> pipeline) {
        return new SyncDivergingPipeline<>(Arrays.asList(this, pipeline));
    }

    static <T> Pipeline<T> end() {
        return EmptyPipeTail.instance();
    }

    class SyncDivergingPipeline<T> implements Pipeline<T> {

        private final Collection<Pipeline<? super T>> mPipelines;

        private SyncDivergingPipeline(Collection<Pipeline<? super T>> pipelines) {
            mPipelines = pipelines;
        }

        @Override
        public void process(T input) throws ProcessingException {
            for (Pipeline<? super T> pipeline : mPipelines) {
                pipeline.process(input);
            }
        }
    }

    final class EmptyPipeTail {

        private static final Pipeline INSTANCE = input -> { };

        @SuppressWarnings("unchecked")
        static <T> Pipeline<T> instance() {
            return (Pipeline<T>) INSTANCE;
        }

        private EmptyPipeTail() {}
    }
}
