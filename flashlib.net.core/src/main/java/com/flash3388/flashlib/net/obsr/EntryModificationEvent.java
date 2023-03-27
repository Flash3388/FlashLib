package com.flash3388.flashlib.net.obsr;

import com.notifier.Event;

public class EntryModificationEvent implements Event {

    private final StoredEntry mEntry;
    private final String mPath;
    private final Value mValue;
    private final ModificationType mType;

    public EntryModificationEvent(StoredEntry entry, String path, Value value, ModificationType type) {
        mEntry = entry;
        mPath = path;
        mValue = value;
        mType = type;
    }

    public StoredEntry getEntry() {
        return mEntry;
    }

    public String getPath() {
        return mPath;
    }

    public Value getValue() {
        return mValue;
    }

    public ModificationType getType() {
        return mType;
    }
}
