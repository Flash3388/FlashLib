package com.flash3388.flashlib.net.obsr;

public class BasicEntry {

    private final EntryType mType;
    private final Object mValue;

    public BasicEntry(EntryType type, Object value) {
        mType = type;
        mValue = value;
    }

    public EntryType getType() {
        return mType;
    }

    public Object getValue() {
        return mValue;
    }
}
