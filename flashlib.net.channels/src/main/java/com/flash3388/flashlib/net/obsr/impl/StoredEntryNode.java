package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.obsr.EntryValueType;
import com.flash3388.flashlib.time.Time;

public class StoredEntryNode {

    private EntryValueType mType;
    private Object mValue;
    private Time mLastChangeTimestamp;

    public StoredEntryNode() {
        mType = EntryValueType.EMPTY;
        mValue = null;
        mLastChangeTimestamp = Time.INVALID;
    }

    public EntryValueType getType() {
        return mType;
    }

    public Object getValue() {
        return mValue;
    }

    public void setValue(EntryValueType type, Object value, Time currentTime, boolean force) {
        if (!force) {
            ensureTypeValid(type, value);
        }

        mType = type;
        mValue = value;
        mLastChangeTimestamp = currentTime;
    }

    private void ensureTypeValid(EntryValueType type, Object value) {
        switch (type) {
            case EMPTY:
                if (value != null) {
                    throw new TypeMismatchException(type, "expected null value, got " + value.getClass().getName());
                }
                break;
            case RAW:
                ensureType(type, byte[].class, value);
                break;
            case BOOLEAN:
                ensureType(type, Boolean.class, value);
                break;
            case INT:
                ensureType(type, Integer.class, value);
                break;
            case DOUBLE:
                ensureType(type, Double.class, value);
                break;
            case STRING:
                ensureType(type, String.class, value);
                break;
            default:
                throw new IllegalArgumentException("unsupported type: " + type);
        }


    }

    private static <T> void ensureType(EntryValueType type, Class<?> expected, Object value) {
        if (!expected.isInstance(value)) {
            throw new TypeMismatchException(type, expected, value.getClass());
        }
    }
}
