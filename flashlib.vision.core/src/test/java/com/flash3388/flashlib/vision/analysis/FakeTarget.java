package com.flash3388.flashlib.vision.analysis;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class FakeTarget implements Target {

    private final Map<String, Object> mProperties;

    public FakeTarget(Map<String, Object> properties) {
        mProperties = properties;
    }

    @Override
    public boolean hasProperty(String name) {
        return mProperties.containsKey(name);
    }

    @Override
    public <T> T getProperty(String name, Class<T> type) {
        Object value = mProperties.get(name);
        if (value == null) {
            throw new NoSuchElementException(name);
        }

        return type.cast(value);
    }

    public Map<String, Object> getProperties() {
        return mProperties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FakeTarget target = (FakeTarget) o;
        return Objects.equals(mProperties, target.mProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mProperties);
    }
}
