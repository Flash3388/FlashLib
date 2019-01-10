package edu.flash3388.flashlib.vision;

import edu.flash3388.flashlib.util.collections.DoubleBuffer;

import java.util.NoSuchElementException;

public class DoubleBufferImageHolder<T extends Image> implements ImageSource<T>, ImagePipeline<T> {

    private final DoubleBuffer<T> mBuffer;

    public DoubleBufferImageHolder() {
        mBuffer = new DoubleBuffer<>();
    }

    @Override
    public void process(T image) {
        mBuffer.write(image);
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