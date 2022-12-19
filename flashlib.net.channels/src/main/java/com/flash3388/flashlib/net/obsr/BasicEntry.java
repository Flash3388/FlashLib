package com.flash3388.flashlib.net.obsr;

public class BasicEntry {

    private final EntryValueType mType;
    private final Object mValue;

    public BasicEntry(EntryValueType type, Object value) {
        mType = type;
        mValue = value;
    }

    public EntryValueType getType() {
        return mType;
    }

    public Object getValue() {
        return mValue;
    }
}
