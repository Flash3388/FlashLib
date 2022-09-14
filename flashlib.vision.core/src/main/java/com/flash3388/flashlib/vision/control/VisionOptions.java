package com.flash3388.flashlib.vision.control;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class VisionOptions {

    private final Map<VisionOption, Object> mOptions;

    public VisionOptions() {
        mOptions = new ConcurrentHashMap<>();
    }

    public <T> void put(VisionOption option, T value) {
        if (!option.valueType().isInstance(value)) {
            throw new IllegalArgumentException("Bad value for option. Doesn't match type");
        }

        mOptions.put(option, value);
    }

    public <T> Optional<T> get(VisionOption option, Class<T> type) {
        Object value = mOptions.get(option);
        if (value == null) {
            return Optional.empty();
        }

        T valueT = option.valueType().convertTo(value, type);
        return Optional.of(valueT);
    }

    public <T> T getOrDefault(VisionOption option, Class<T> type, T defaultValue) {
        Object value = mOptions.get(option);
        if (value == null) {
            return defaultValue;
        }

        return option.valueType().convertTo(value, type);
    }
}
