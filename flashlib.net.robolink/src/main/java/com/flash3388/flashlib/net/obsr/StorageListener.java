package com.flash3388.flashlib.net.obsr;

import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.Value;

public interface StorageListener {

    void onEntryUpdate(StoragePath path, Value value);
    void onEntryClear(StoragePath path);
    void onEntryDeleted(StoragePath path);
}
