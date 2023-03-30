package com.flash3388.flashlib.net.channels.messsaging;

import com.flash3388.flashlib.net.messaging.MessageType;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class KnownMessageTypes {

    private final Map<Integer, MessageType> mTypeMap;

    public KnownMessageTypes() {
        mTypeMap = new ConcurrentHashMap<>();
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
