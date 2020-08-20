package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.vision.processing.ProcessingException;

import java.util.Arrays;
import java.util.Collection;

@FunctionalInterface
public interface Pipeline<T extends Image> {

    void process(T image) throws ProcessingException;

    default Pipeline<T> divergeTo(Pipeline<? super T> pipeline) {
        return new SyncDivergingPipeline<>(Arrays.asList(this, pipeline));
    }

    static <T extends Image> Pipeline<T> end() {
        return EmptyPipeTail.instance();
    }

    class SyncDivergingPipeline<T extends Image> implements Pipeline<T> {

        private final Collection<Pipeline<? super T>> mPipelines;

        private SyncDivergingPipeline(Collection<Pipeline<? super T>> pipelines) {
            mPipelines = pipelines;
        }

        @Override
        public void process(T image) throws ProcessingException {
            for (Pipeline<? super T> pipeline : mPipelines) {
                pipeline.process(image);
            }
        }
    }

    final class EmptyPipeTail {

        private static final Pipeline INSTANCE = image -> { };

        @SuppressWarnings("unchecked")
        static <T extends Image> Pipeline<T> instance() {
            return (Pipeline<T>) INSTANCE;
        }

        private EmptyPipeTail() {}
    }
}
