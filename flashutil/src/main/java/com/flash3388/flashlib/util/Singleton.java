package com.flash3388.flashlib.util;

import com.beans.Property;
import com.flash3388.flashlib.util.resources.Resource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class Singleton<T> implements Property<T>, Resource {

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

    @Override
    public void free() {
        mInstanceReference.set(null);
    }
}
