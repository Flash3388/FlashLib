package com.flash3388.flashlib.vision;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class ImageQueue<T extends Image> implements ImageSource<T> {

    private final Queue<T> mImageQueue;

    public ImageQueue(Queue<T> imageQueue) {
        mImageQueue = imageQueue;
    }

    @SafeVarargs
    public ImageQueue(T... images) {
        this(new ArrayDeque<>(Arrays.asList(images)));
    }

    @Override
    public T get() throws VisionException {
        if (mImageQueue.isEmpty()) {
            throw new VisionException("no more images");
        }
        return mImageQueue.remove();
    }
}
