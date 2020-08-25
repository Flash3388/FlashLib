package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;

public class InProcessorJunction<T, R> implements Processor<T, R> {

    private final Processor<T, R> mProcessor;
    private final Pipeline<? super T> mPipeline;

    public InProcessorJunction(Processor<T, R> processor, Pipeline<? super T> pipeline) {
        mProcessor = processor;
        mPipeline = pipeline;
    }

    @Override
    public R process(T input) throws VisionException {
        mPipeline.process(input);
        return mProcessor.process(input);
    }
}
