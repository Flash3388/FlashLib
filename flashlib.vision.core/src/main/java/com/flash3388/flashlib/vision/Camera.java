package com.flash3388.flashlib.vision;

public interface Camera<T extends Image> extends Source<T>, AutoCloseable {

    int getFps();

    int getHeight();
    int getWidth();

    T capture() throws VisionException;

    @Override
    default T get() throws VisionException {
        return capture();
    }
}
