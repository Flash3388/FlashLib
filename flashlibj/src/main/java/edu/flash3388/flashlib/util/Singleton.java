package edu.flash3388.flashlib.util;

import com.beans.Property;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Singleton<T> implements Property<T> {

    private final AtomicReference<T> mInstanceReference;

    public Singleton(T value) {
        mInstanceReference = new AtomicReference<>(value);
    }

    public Singleton() {
        this(null);
    }

    @Override
    public T get() {
        T value = mInstanceReference.get();
        if (value == null) {
            throw new NullPointerException("singleton not initialized");
        }

        return value;
    }

    @Override
    public void set(T value) {
        Objects.requireNonNull(value, "singleton cannot accept null");
        mInstanceReference.set(value);
    }

    public void clear() {
        mInstanceReference.set(null);
    }
}
