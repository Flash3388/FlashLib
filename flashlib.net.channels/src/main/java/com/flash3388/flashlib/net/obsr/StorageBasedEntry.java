package com.flash3388.flashlib.net.obsr;

import java.lang.ref.WeakReference;
import java.util.Optional;

public class StorageBasedEntry implements StoredEntry {

    private final StoragePath mPath;
    private final WeakReference<Storage> mStorage;

    public StorageBasedEntry(StoragePath path, Storage storage) {
        mPath = path;
        mStorage = new WeakReference<>(storage);
    }

    @Override
    public EntryType getType() {
        Storage storage = getStorage();
        return storage.getEntryType(mPath);
    }

    @Override
    public boolean isEmpty() {
        return getType() == EntryType.EMPTY;
    }

    @Override
    public byte[] getRaw(byte[] defaultValue) {
        return getValueForType(EntryType.RAW, byte[].class, defaultValue);
    }

    @Override
    public boolean getBoolean(boolean defaultValue) {
        return getValueForType(EntryType.BOOLEAN, Boolean.class, defaultValue);
    }

    @Override
    public int getInt(int defaultValue) {
        return getValueForType(EntryType.INT, Integer.class, defaultValue);
    }

    @Override
    public double getDouble(double defaultValue) {
        return getValueForType(EntryType.DOUBLE, Double.class, defaultValue);
    }

    @Override
    public String getString(String defaultValue) {
        return getValueForType(EntryType.STRING, String.class, defaultValue);
    }

    @Override
    public void clearValue() {
        Storage storage = getStorage();
        storage.clearEntryValue(mPath);
    }

    @Override
    public void setRaw(byte[] value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryType.RAW, value);
    }

    @Override
    public void setBoolean(boolean value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryType.BOOLEAN, value);
    }

    @Override
    public void setInt(int value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryType.INT, value);
    }

    @Override
    public void setDouble(double value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryType.DOUBLE, value);
    }

    @Override
    public void setString(String value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryType.STRING, value);
    }

    private <T> T getValueForType(EntryType type, Class<T> clsType, T defaultValue) {
        Storage storage = getStorage();
        Optional<Object> optional = storage.getEntryValueForType(mPath, type);
        return optional.map(clsType::cast).orElse(defaultValue);
    }

    private Storage getStorage() {
        Storage storage = mStorage.get();
        if (storage == null) {
            throw new IllegalStateException("storage was garbage collected");
        }

        return storage;
    }
}
