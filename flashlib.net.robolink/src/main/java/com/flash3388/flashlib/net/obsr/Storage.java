package com.flash3388.flashlib.net.obsr;

import com.beans.observables.RegisteredListener;
import com.flash3388.flashlib.net.messaging.NewMessageEvent;

import java.util.Map;

public interface Storage {

    Map<String, Value> getAll();

    StoredObject getObject(StoragePath path);
    void deleteObject(StoragePath path);

    RegisteredListener addListener(StoragePath path, EntryListener listener);
    StoredEntry getEntry(StoragePath path);
    ValueProperty getEntryValueProperty(StoragePath path);
    Value getEntryValue(StoragePath path);
    void setEntryValue(StoragePath path, Value value);
    void clearEntryValue(StoragePath path);
    void deleteEntry(StoragePath path);

    void updateFromMessage(NewMessageEvent event);
}
