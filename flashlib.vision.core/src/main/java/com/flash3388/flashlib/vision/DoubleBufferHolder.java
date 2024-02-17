package com.flash3388.flashlib.vision;

import com.flash3388.flashlib.util.collections.DoubleBuffer;

import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DoubleBufferHolder<T> implements Source<T>, Pipeline<T> {

    private final DoubleBuffer<T> mBuffer;
    private final Lock mLock;
    private final Condition mNewData;

    public DoubleBufferHolder() {
        mBuffer = new DoubleBuffer<>();
        mLock = new ReentrantLock();
        mNewData = mLock.newCondition();
    }

    @Override
    public void process(T input) {
        mLock.lock();
        try {
            mBuffer.write(input);
            mNewData.signalAll();
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public T get() throws VisionException {
        mLock.lock();
        try {
            while (true) {
                try {
                    return mBuffer.read();
                } catch (NoSuchElementException e) {
                    try {
                        mNewData.await();
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                        throw new VisionException(ex);
                    }
                }
            }
        } finally {
            mLock.unlock();
        }
    }
}
