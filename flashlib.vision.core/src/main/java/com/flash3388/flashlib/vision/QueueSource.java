package com.flash3388.flashlib.vision;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class QueueSource<T extends Image> implements Source<T> {

    private final Queue<T> mImageQueue;

    public QueueSource(Queue<T> imageQueue) {
        mImageQueue = imageQueue;
    }

    @SafeVarargs
    public QueueSource(T... images) {
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
