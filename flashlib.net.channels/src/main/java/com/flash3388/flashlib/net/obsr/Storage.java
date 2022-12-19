package com.flash3388.flashlib.net.obsr;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

public interface Storage {

    Map<String, BasicEntry> getAll();
    void setAll(Map<String, BasicEntry> values);

    StoredObject getObject(StoragePath path);

    void createEntry(StoragePath path, EnumSet<StorageOpFlag> flags);
    StoredEntry getEntry(StoragePath path);
    EntryType getEntryType(StoragePath path);
    Optional<Object> getEntryValueForType(StoragePath path, EntryType type);
    void setEntryValue(StoragePath path, EntryType type, Object value, EnumSet<StorageOpFlag> flags);
    void clearEntryValue(StoragePath path, EnumSet<StorageOpFlag> flags);

    default void createEntry(StoragePath path) {
        createEntry(path, EnumSet.noneOf(StorageOpFlag.class));
    }

    default void setEntryValue(StoragePath path, EntryType type, Object value) {
        setEntryValue(path, type, value, EnumSet.noneOf(StorageOpFlag.class));
    }

    default void clearEntryValue(StoragePath path) {
        clearEntryValue(path, EnumSet.noneOf(StorageOpFlag.class));
    }
}
