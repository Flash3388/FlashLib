package com.flash3388.flashlib.io.devices;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class GroupBuilder<E, T extends DeviceGroup<E>> {

    private final DeviceInterface mDeviceInterface;
    private final Function<List<E>, T> mCreator;
    private final List<E> mParts;
    private final Class<E> mElementType;

    GroupBuilder(DeviceInterface deviceInterface, Function<List<E>, T> creator, Class<E> elementType) {
        mDeviceInterface = deviceInterface;
        mCreator = creator;
        mElementType = elementType;
        mParts = new ArrayList<>();
    }

    public GroupBuilder<E, T> add(E e) {
        mParts.add(e);
        return this;
    }

    /**
     * Finds a registered implementation for the given ID which matches the wanted type.
     * Once the implementation is found, a constructor is matched to the given arguments
     * count and an instance is created.
     *
     * @param id implementation identifier
     * @param type type which the implementation must support (usually interface)
     * @param namedArgs arguments to the constructor
     * @return this
     * @param <E2> type which the implementation must support
     *
     * @see DeviceInterface#newDevice(int, Class, Map)
     */
    public <E2 extends E> GroupBuilder<E, T> addNewDevice(int id, Class<E2> type, Map<String, Object> namedArgs) {
        E2 device = mDeviceInterface.newDevice(id, type, namedArgs);
        return add(device);
    }

    /**
     * Finds a registered implementation for the given ID which matches the wanted type.
     * Once the implementation is found, a constructor is matched to the given arguments
     * count and an instance is created.
     *
     * @param id implementation identifier
     * @param namedArgs arguments to the constructor
     * @return this
     *
     * @see DeviceInterface#newDevice(int, Class, Map)
     */
    public GroupBuilder<E, T> addNewDevice(int id, Map<String, Object> namedArgs) {
        return addNewDevice(id, mElementType, namedArgs);
    }

    /**
     * Finds a registered implementation for the given ID which matches the wanted type.
     * Once the implementation is found, a constructor is matched to the given arguments
     * count and an instance is created.
     *
     * @param id implementation identifier
     * @param namedArgs arguments to the constructor
     * @return this
     *
     * @see DeviceInterface#newDevice(int, Class, Map)
     */
    public <T2 extends T> GroupBuilder<E, T> addNewDevice(DeviceId<T2> id, Map<String, Object> namedArgs) {
        return addNewDevice(id.id(), mElementType, namedArgs);
    }

    public T build() {
        return mCreator.apply(mParts);
    }
}
