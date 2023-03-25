package com.flash3388.flashlib.net.obsr;

import java.util.EnumSet;
import java.util.Map;

public interface Storage {

    Map<String, Value> getAll();
    void setAll(Map<String, Value> values);

    StoredObject getObject(StoragePath path);

    void createEntry(StoragePath path, EnumSet<StorageOpFlag> flags);
    StoredEntry getEntry(StoragePath path);
    Value getEntryValue(StoragePath path);
    void setEntryValue(StoragePath path, Value value, EnumSet<StorageOpFlag> flags);
    void clearEntryValue(StoragePath path, EnumSet<StorageOpFlag> flags);

    ValueProperty getEntryValueProperty(StoragePath path);

    default void createEntry(StoragePath path) {
        createEntry(path, EnumSet.noneOf(StorageOpFlag.class));
    }

    default void setEntryValue(StoragePath path, Value value) {
        setEntryValue(path, value, EnumSet.noneOf(StorageOpFlag.class));
    }

    default void clearEntryValue(StoragePath path) {
        clearEntryValue(path, EnumSet.noneOf(StorageOpFlag.class));
    }

    void addListener(StoragePath path, ObjectListener listener);
    void removeListener(StoragePath path, ObjectListener listener);
}
