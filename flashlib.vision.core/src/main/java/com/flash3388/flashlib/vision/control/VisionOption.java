package com.flash3388.flashlib.vision.control;

public interface VisionOption<T> {

    String name();
    Class<T> valueType();

    class Impl<T> implements VisionOption<T> {

        private final String mName;
        private final Class<T> mValueType;

        public Impl(String name, Class<T> valueType) {
            mName = name;
            mValueType = valueType;
        }

        @Override
        public String name() {
            return mName;
        }

        @Override
        public Class<T> valueType() {
            return mValueType;
        }
    }
}
