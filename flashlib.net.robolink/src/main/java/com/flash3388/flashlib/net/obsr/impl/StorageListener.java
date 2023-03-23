package com.flash3388.flashlib.net.obsr.impl;

import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.Value;

public interface StorageListener {

    void onNewEntry(StoragePath path);
    void onEntryUpdate(StoragePath path, Value value);
    void onEntryClear(StoragePath path);
}
