package edu.flash3388.flashlib.util.collections;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.IntUnaryOperator;

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
        T value = mArray.getAndSet(mReadIndex.getAndUpdate(mReadIndexUpdater), null);

        if (value == null) {
            throw new NoSuchElementException("nothing to read");
        }

        return value;
    }

    public void write(T value) {
        mArray.set(mReadIndexUpdater.applyAsInt(mReadIndex.get()), value);
    }

    private static class XorUpdater implements IntUnaryOperator {

        @Override
        public int applyAsInt(int operand) {
            return operand ^ 1;
        }
    }
}
