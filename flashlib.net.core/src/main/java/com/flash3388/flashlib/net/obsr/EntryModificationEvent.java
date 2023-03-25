package com.flash3388.flashlib.net.obsr;

import com.notifier.Event;

public class EntryModificationEvent implements Event {

    private final StoredEntry mEntry;
    private final String mPath;

    public EntryModificationEvent(StoredEntry entry, String path) {
        mEntry = entry;
        mPath = path;
    }

    public StoredEntry getEntry() {
        return mEntry;
    }

    public String getPath() {
        return mPath;
    }
}
