package com.flash3388.flashlib.net.messaging.data;

import com.flash3388.flashlib.net.messaging.MessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class KnownMessageTypes {

    private final Map<Integer, MessageType> mTypeMap;

    public KnownMessageTypes() {
        mTypeMap = new HashMap<>();
    }

    public void put(MessageType type) {
        mTypeMap.put(type.getKey(), type);
    }

    public MessageType get(int key) {
        MessageType type = mTypeMap.get(key);
        if (type == null) {
            throw new NoSuchElementException(String.valueOf(key));
        }

        return type;
    }
}
