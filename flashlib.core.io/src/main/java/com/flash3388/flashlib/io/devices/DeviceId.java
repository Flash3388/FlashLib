package com.flash3388.flashlib.io.devices;

public interface DeviceId<T> {

    int id();
    Class<T> baseType();

    static <T> DeviceId<T> of(int id, Class<T> baseType) {
        return new Impl<>(id, baseType);
    }

    class Impl<T> implements DeviceId<T> {

        private final int mId;
        private final Class<T> mBaseType;

        public Impl(int id, Class<T> baseType) {
            mId = id;
            mBaseType = baseType;
        }

        @Override
        public int id() {
            return mId;
        }

        @Override
        public Class<T> baseType() {
            return mBaseType;
        }
    }
}
