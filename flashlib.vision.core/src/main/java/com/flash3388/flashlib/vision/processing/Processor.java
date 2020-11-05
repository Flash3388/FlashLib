package com.flash3388.flashlib.vision.processing;

import com.castle.util.throwables.ThrowableHandler;
import com.flash3388.flashlib.vision.Pipeline;
import com.flash3388.flashlib.vision.VisionException;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Processor<T, R> {

    R process(T input) throws VisionException;

    default <R2> Processor<T, R2> andThen(Processor<? super R, R2> processor) {
        return ProcessorChain.create(this, processor);
    }

    default Pipeline<T> pipeTo(Pipeline<? super R> pipeline) {
        return new ProcessorEnd<>(this, pipeline);
    }

    default Processor<T, R> divergeIn(Pipeline<? super T> pipeline) {
        return new InProcessorJunction<>(this, pipeline);
    }

    default Processor<T, R> divergeOut(Pipeline<? super R> pipeline) {
        return new OutProcessorJunction<>(this, pipeline);
    }

    default Function<T, R> asFunction(ThrowableHandler throwableHandler) {
        return (input)-> {
            try {
                return this.process(input);
            } catch (VisionException e) {
                throwableHandler.handle(e);
                return null;
            }
        };
    }

    static <T, R> Processor<T, R> mapper(Function<T, R> mapper) {
        return new MappingProcessor<>(mapper);
    }
}
