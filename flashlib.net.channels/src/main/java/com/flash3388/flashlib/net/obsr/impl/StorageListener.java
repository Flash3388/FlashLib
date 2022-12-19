package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.obsr.EntryType;
import com.flash3388.flashlib.net.obsr.StoragePath;

public interface StorageListener {

    void onNewEntry(StoragePath path);
    void onEntryUpdate(StoragePath path, EntryType type, Object value);
    void onEntryClear(StoragePath path);
}
