package com.flash3388.flashlib.util.resources;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class CircularResourceHolder<T> {

    private final Supplier<T> mInstanceCreator;
    private final Queue<T> mQueue;
    private final Lock mLock;

    public CircularResourceHolder(int initialCount, Supplier<T> instanceCreator) {
        mInstanceCreator = instanceCreator;
        mQueue = new ArrayDeque<>(initialCount);
        mLock = new ReentrantLock();

        for (int i = 0; i < initialCount; i++) {
            add(instanceCreator.get());
        }
    }

    public void add(T instance) {
        if (instance == null) {
            return;
        }

        mLock.lock();
        try {
            mQueue.add(instance);
        } finally {
            mLock.unlock();
        }
    }

    public T retrieve() {
        mLock.lock();
        try {
            T instance = mQueue.poll();
            if (instance == null) {
                instance = mInstanceCreator.get();
            }

            return instance;
        } finally {
            mLock.unlock();
        }
    }
}
