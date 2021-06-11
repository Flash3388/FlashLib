package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@FunctionalInterface
public interface Pipeline<T> {

    void process(T input) throws VisionException;

    default Pipeline<T> divergeTo(Pipeline<? super T> pipeline) {
        return new SyncPipeJunction<>(this, pipeline);
    }

    default <T2> Pipeline<T> mapTo(Pipeline<? super T2> pipeline, Function<? super T, ? extends T2> mapper) {
        return divergeTo(new MappingPipeline<>(pipeline, mapper));
    }

    default Pipeline<T> filterTo(Predicate<? super T> filter, Pipeline<? super T> pipeline) {
        return divergeTo(new FilterPipeline<>(filter, pipeline));
    }

    default Consumer<T> asConsumer(ThrowableHandler throwableHandler) {
        return (input)-> {
            try {
                this.process(input);
            } catch (VisionException e) {
                throwableHandler.handle(e);
            }
        };
    }

    static <T, T2> Pipeline<T> mapper(Pipeline<? super T2> pipeline, Function<? super T, ? extends T2> mapper) {
        return new MappingPipeline<>(pipeline, mapper);
    }
}
