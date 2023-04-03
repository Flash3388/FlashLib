package com.flash3388.flashlib.vision.control;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class KnownVisionOptionTypes {

    private final Map<String, VisionOption<?>> mTypeMap;

    public KnownVisionOptionTypes(Collection<VisionOption<?>> types) {
        mTypeMap = new ConcurrentHashMap<>();
        types.forEach(this::put);
    }

    public KnownVisionOptionTypes(VisionOption<?>... types) {
        this(Arrays.asList(types));
    }

    public KnownVisionOptionTypes() {
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
