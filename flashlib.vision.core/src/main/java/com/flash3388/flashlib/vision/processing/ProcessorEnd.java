package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;

public class ProcessorEnd<T, R> implements Pipeline<T> {

    private final Processor<? super T, ? extends R> mProcessor;
    private final Pipeline<? super R> mPipeline;

    public ProcessorEnd(Processor<? super T, ? extends R> processor, Pipeline<? super R> pipeline) {
        mProcessor = processor;
        mPipeline = pipeline;
    }

    @Override
    public void process(T input) throws VisionException {
        R out = mProcessor.process(input);
        mPipeline.process(out);
    }
}
