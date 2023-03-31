package com.flash3388.flashlib.io.devices;

import com.flash3388.flashlib.annotations.MainThreadOnly;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * A provider/creator of devices of certain types. Each registered implementation
 * has an identifier to find it.
 *
 * @since FlashLib 3.2.0
 */
public interface DeviceInterface {

    /**
     * Finds a registered implementation for the given ID which matches the wanted type.
     * Once the implementation is found, a constructor is matched to the given arguments
     * count and an instance is created.
     *
     * @param id implementation identifier
     * @param type type which the implementation must support (usually interface)
     * @param namedArgs arguments to the constructor
     * @return created instance
     * @param <T> type which the implementation must support
     */
    @MainThreadOnly
    <T> T newDevice(String id, Class<T> type, Map<String, Object> namedArgs);

    /**
     * Creates a new group of devices, which is encapsulated under a single interface.
     *
     * @return an object encapsulating usage of all the device
     */
    @MainThreadOnly
    <E, T extends DeviceGroup<E>> GroupBuilder<E, T> newGroup(Class<T> groupType, Function<List<E>, T> creator);
}
