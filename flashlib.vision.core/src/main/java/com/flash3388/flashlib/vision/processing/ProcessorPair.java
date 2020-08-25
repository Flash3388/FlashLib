package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.VisionException;

public class ProcessorPair<T, R, R2> implements Processor<T, R2> {

    private final Processor<T, R> mIn;
    private final Processor<? super R, R2> mOut;

    public ProcessorPair(Processor<T, R> in, Processor<? super R, R2> out) {
        mIn = in;
        mOut = out;
    }

    @Override
    public R2 process(T input) throws VisionException {
        R out = mIn.process(input);
        return mOut.process(out);
    }
}
