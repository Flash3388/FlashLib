package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.util.collections.DoubleBuffer;

import java.util.NoSuchElementException;

public class DoubleBufferHolder<T> implements Source<T>, Pipeline<T> {

    private final DoubleBuffer<T> mBuffer;

    public DoubleBufferHolder() {
        mBuffer = new DoubleBuffer<>();
    }

    @Override
    public void process(T input) {
        mBuffer.write(input);
    }

    @Override
    public T get() throws VisionException {
        try {
            return mBuffer.read();
        } catch (NoSuchElementException e) {
            throw new VisionException(e);
        }
    }
}
