package com.flash3388.flashlib.vision.control;

import com.castle.reflect.DataType;

public interface VisionOption {

    String name();
    DataType valueType();

    static VisionOption create(String name, DataType valueType) {
        return new Impl(name, valueType);
    }

    class Impl implements VisionOption {

        private final String mName;
        private final DataType mValueType;

        public Impl(String name, DataType valueType) {
            mName = name;
            mValueType = valueType;
        }

        @Override
        public String name() {
            return mName;
        }

        @Override
        public DataType valueType() {
            return mValueType;
        }
    }
}
