package com.flash3388.flashlib.vision.control;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class KnownVisionOptions {

    private final Map<String, VisionOption<?>> mTypeMap;

    public KnownVisionOptions(Collection<VisionOption<?>> types) {
        mTypeMap = new ConcurrentHashMap<>();
        types.forEach(this::put);
    }

    public KnownVisionOptions(VisionOption<?>... types) {
        this(Arrays.asList(types));
    }

    public KnownVisionOptions() {
        this(Collections.emptyList());
    }

    public void put(VisionOption<?> type) {
        mTypeMap.put(type.name(), type);
    }

    public VisionOption<?> get(String key) {
        VisionOption<?> type = mTypeMap.get(key);
        if (type == null) {
            throw new NoSuchElementException(key);
        }

        return type;
    }
}
