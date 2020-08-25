package com.flash3388.flashlib.vision;

@FunctionalInterface
public interface Pipeline<T> {

    void process(T input) throws VisionException;

    default Pipeline<T> divergeTo(Pipeline<? super T> pipeline) {
        return new SyncPipeJunction<>(this, pipeline);
    }

}
