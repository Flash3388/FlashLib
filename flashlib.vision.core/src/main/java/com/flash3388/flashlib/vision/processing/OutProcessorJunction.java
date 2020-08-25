package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;

public class OutProcessorJunction<T, R> implements Processor<T, R> {

    private final Processor<T, R> mProcessor;
    private final Pipeline<? super R> mPipeline;

    public OutProcessorJunction(Processor<T, R> processor, Pipeline<? super R> pipeline) {
        mProcessor = processor;
        mPipeline = pipeline;
    }

    @Override
    public R process(T input) throws VisionException {
        R out = mProcessor.process(input);
        mPipeline.process(out);
        return out;
    }
}
