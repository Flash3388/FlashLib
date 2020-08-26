package com.flash3388.flashlib.vision;

import com.castle.util.throwables.ThrowableHandler;

import java.util.function.Consumer;

@FunctionalInterface
public interface Pipeline<T> {

    void process(T input) throws VisionException;

    default Pipeline<T> divergeTo(Pipeline<? super T> pipeline) {
        return new SyncPipeJunction<>(this, pipeline);
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
}
