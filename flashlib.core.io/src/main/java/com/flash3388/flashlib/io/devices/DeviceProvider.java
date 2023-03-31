package com.flash3388.flashlib.io.devices;

public interface DeviceProvider {

    <T> Class<? extends T> findDevice(String id, Class<T> type);
}
