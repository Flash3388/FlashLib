package com.flash3388.flashlib.vision.control;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class VisionOptions {

    private final Map<VisionOption<?>, Object> mOptions;

    public VisionOptions() {
        mOptions = new ConcurrentHashMap<>();
    }

    public <T> void put(VisionOption<T> option, T value) {
        mOptions.put(option, value);
    }

    public <T> Optional<T> get(VisionOption<T> option) {
        Object value = mOptions.get(option);
        if (value == null) {
            return Optional.empty();
        }

        T valueT = option.valueType().cast(value);
        return Optional.of(valueT);
    }

    public <T> T getOrDefault(VisionOption<T> option, T defaultValue) {
        Object value = mOptions.get(option);
        if (value == null) {
            return defaultValue;
        }

        return option.valueType().cast(value);
    }
}
