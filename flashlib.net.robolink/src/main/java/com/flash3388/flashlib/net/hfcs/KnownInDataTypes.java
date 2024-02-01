package com.flash3388.flashlib.net.hfcs;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class KnownInDataTypes {

    private final Map<Integer, HfcsInType<?>> mTypeMap;

    public KnownInDataTypes() {
        mTypeMap = new ConcurrentHashMap<>();
    }

    public void put(HfcsInType<?> type) {
        mTypeMap.put(type.getKey(), type);
    }

    public HfcsInType<?> get(int key) {
        HfcsInType<?> type = mTypeMap.get(key);
        if (type == null) {
            throw new NoSuchElementException(String.valueOf(key));
        }

        return type;
    }
}
