package com.flash3388.flashlib.vision.processing;

import com.flash3388.flashlib.vision.VisionException;

import java.util.Arrays;
import java.util.Collection;

public class ProcessorChain implements Processor {

    private final Collection<Processor> mProcessors;

    ProcessorChain(Collection<Processor> processors) {
        mProcessors = processors;
    }

    ProcessorChain(Processor... processors) {
        this(Arrays.asList(processors));
    }

    @SuppressWarnings("unchecked")
    public static <T, R, R2> Processor<T, R2> create(Processor<T, R> in, Processor<? super R, R2> out) {
        return new ProcessorChain(in, out);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object process(Object input) throws VisionException {
        Object out = input;
        for (Processor processor : mProcessors) {
            out = processor.process(out);
        }
        return out;
    }

    @Override
    public Processor pipeTo(Processor processor) {
        mProcessors.add(processor);
        return this;
    }
}
