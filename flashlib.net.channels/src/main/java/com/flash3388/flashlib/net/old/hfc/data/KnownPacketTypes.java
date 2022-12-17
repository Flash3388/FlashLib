package com.flash3388.flashlib.net.old.hfc.data;

import com.flash3388.flashlib.net.hfc.PacketType;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

public class KnownPacketTypes {

    private final Map<Integer, PacketType> mMap;

    public KnownPacketTypes() {
        mMap = new ConcurrentHashMap<>();
    }

    public PacketType get(int type) {
        PacketType packetType = mMap.get(type);
        if (packetType == null) {
            throw new NoSuchElementException();
        }

        return packetType;
    }

    public void put(PacketType type) {
        mMap.put(type.getKey(), type);
    }
}
