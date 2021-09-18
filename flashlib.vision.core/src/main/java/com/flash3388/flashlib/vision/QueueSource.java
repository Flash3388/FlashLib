package com.flash3388.flashlib.vision;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;

public class QueueSource<T> implements Source<T> {

    private final Queue<T> mQueue;

    public QueueSource(Queue<T> queue) {
        mQueue = queue;
    }

    @SafeVarargs
    public QueueSource(T... objects) {
        this(new ArrayDeque<>(Arrays.asList(objects)));
    }

    @Override
    public T get() throws VisionException {
        if (mQueue.isEmpty()) {
            throw new VisionException("no more objects in queue");
        }

        return mQueue.remove();
    }
}
