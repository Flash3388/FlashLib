package com.flash3388.flashlib.util.collections;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntUnaryOperator;

/**
 * A single-consumer, single-producer {@code DoubleBuffer}.
 *
 * @param <T> type of data stored
 */
public class DoubleBuffer<T> {

    private final AtomicReferenceArray<T> mArray;
    private final AtomicInteger mReadIndex;
    private final IntUnaryOperator mReadIndexUpdater;

    DoubleBuffer(AtomicReferenceArray<T> array, AtomicInteger readIndex) {
        mArray = array;
        mReadIndex = readIndex;
        mReadIndexUpdater = new XorUpdater();
    }

    public DoubleBuffer() {
        this(new AtomicReferenceArray<>(2), new AtomicInteger(0));
    }

    public T read() {
        T value = mArray.get(mReadIndex.get());

        if (value == null) {
            throw new NoSuchElementException("nothing to read");
        }

        return value;
    }

    public void write(T value) {
        int index = mReadIndex.get();
        mArray.set(index, value);
        mReadIndex.updateAndGet(mReadIndexUpdater);
    }

    private static class XorUpdater implements IntUnaryOperator {

        @Override
        public int applyAsInt(int operand) {
            return operand ^ 1;
        }
    }
}
