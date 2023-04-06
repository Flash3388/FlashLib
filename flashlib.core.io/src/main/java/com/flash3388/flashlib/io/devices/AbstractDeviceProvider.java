package com.flash3388.flashlib.io.devices;

import com.castle.reflect.exceptions.TypeException;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public abstract class AbstractDeviceProvider implements DeviceProvider {

    protected static final Logger LOGGER = Logging.getMainLogger();

    private final Map<Integer, Class<?>> mKnownTypes;

    protected AbstractDeviceProvider() {
        mKnownTypes = new HashMap<>();
    }

    @Override
    public <T> Class<? extends T> findDevice(int id, Class<T> type) {
        Class<?> storedType = mKnownTypes.get(id);
        if (storedType == null) {
            throw new NoSuchElementException(String.valueOf(id));
        }

        if (storedType.isAssignableFrom(type)) {
            throw new TypeException("stored type is not assignable from wanted type");
        }

        //noinspection unchecked
        return (Class<? extends T>) storedType;
    }

    protected void registerDevice(int id, Class<?> type) {
        type = mKnownTypes.put(id, type);
        if (type != null) {
            LOGGER.warn("Device of id {} override", id);
        }
    }
}
