package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;

public interface Processor<T, R> {

    R process(T input) throws VisionException;

    default <R2> Processor<T, R2> pipeTo(Processor<? super R, R2> processor) {
        return ProcessorChain.create(this, processor);
    }

    default Processor<T, R> divergeIn(Pipeline<? super T> pipeline) {
        return new InProcessorJunction<>(this, pipeline);
    }

    default Processor<T, R> divergeOut(Pipeline<? super R> pipeline) {
        return new OutProcessorJunction<>(this, pipeline);
    }

}
