package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Image;
import com.flash3388.flashlib.vision.Pipeline;

public interface Processor<T extends Image, R extends Image> {

    R process(T image) throws ProcessingException;

    default <R2 extends Image> Processor<T, R2> pipeTo(Processor<? super R, R2> processor) {
        return new ProcessorChain<T, R, R2>(this, processor);
    }

    default Processor<T, R> divergeIn(Pipeline<? super T> pipeline) {
        return new InDiverging<>(this, pipeline);
    }

    default Processor<T, R> pipeOut(Pipeline<? super R> pipeline) {
        return new OutDiverging<>(this, pipeline);
    }

    class ProcessorChain<T extends Image, R extends Image, R2 extends Image> implements Processor<T, R2> {

        private final Processor<T, R> mIn;
        private final Processor<? super R, R2> mOut;

        public ProcessorChain(Processor<T, R> in, Processor<? super R, R2> out) {
            mIn = in;
            mOut = out;
        }

        @Override
        public R2 process(T image) throws ProcessingException {
            R out = mIn.process(image);
            return mOut.process(out);
        }
    }

    class InDiverging<T extends Image, R extends Image> implements Processor<T, R> {

        private final Processor<T, R> mProcessor;
        private final Pipeline<? super T> mPipeline;

        public InDiverging(Processor<T, R> processor, Pipeline<? super T> pipeline) {
            mProcessor = processor;
            mPipeline = pipeline;
        }

        @Override
        public R process(T image) throws ProcessingException {
            mPipeline.process(image);
            return mProcessor.process(image);
        }
    }

    class OutDiverging<T extends Image, R extends Image> implements Processor<T, R> {

        private final Processor<T, R> mProcessor;
        private final Pipeline<? super R> mPipeline;

        public OutDiverging(Processor<T, R> processor, Pipeline<? super R> pipeline) {
            mProcessor = processor;
            mPipeline = pipeline;
        }

        @Override
        public R process(T image) throws ProcessingException {
            R out = mProcessor.process(image);
            mPipeline.process(out);
            return out;
        }
    }
}
