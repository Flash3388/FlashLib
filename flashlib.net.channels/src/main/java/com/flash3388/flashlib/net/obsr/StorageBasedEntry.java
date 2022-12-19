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
    public EntryValueType getType() {
        Storage storage = getStorage();
        return storage.getEntryType(mPath);
    }

    @Override
    public boolean isEmpty() {
        return getType() == EntryValueType.EMPTY;
    }

    @Override
    public byte[] getRaw(byte[] defaultValue) {
        return getValueForType(EntryValueType.RAW, byte[].class, defaultValue);
    }

    @Override
    public boolean getBoolean(boolean defaultValue) {
        return getValueForType(EntryValueType.BOOLEAN, Boolean.class, defaultValue);
    }

    @Override
    public int getInt(int defaultValue) {
        return getValueForType(EntryValueType.INT, Integer.class, defaultValue);
    }

    @Override
    public double getDouble(double defaultValue) {
        return getValueForType(EntryValueType.DOUBLE, Double.class, defaultValue);
    }

    @Override
    public String getString(String defaultValue) {
        return getValueForType(EntryValueType.STRING, String.class, defaultValue);
    }

    @Override
    public void clearValue() {
        Storage storage = getStorage();
        storage.clearEntryValue(mPath);
    }

    @Override
    public void setRaw(byte[] value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryValueType.RAW, value);
    }

    @Override
    public void setBoolean(boolean value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryValueType.BOOLEAN, value);
    }

    @Override
    public void setInt(int value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryValueType.INT, value);
    }

    @Override
    public void setDouble(double value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryValueType.DOUBLE, value);
    }

    @Override
    public void setString(String value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, EntryValueType.STRING, value);
    }

    private <T> T getValueForType(EntryValueType type, Class<T> clsType, T defaultValue) {
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
