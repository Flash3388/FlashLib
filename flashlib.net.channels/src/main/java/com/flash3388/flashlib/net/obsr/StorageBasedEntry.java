package com.flash3388.flashlib.net.obsr;

import java.lang.ref.WeakReference;

public class StorageBasedEntry implements StoredEntry {

    private final StoragePath mPath;
    private final WeakReference<Storage> mStorage;

    public StorageBasedEntry(StoragePath path, Storage storage) {
        mPath = path;
        mStorage = new WeakReference<>(storage);
    }

    @Override
    public Value getValue() {
        Storage storage = getStorage();
        return storage.getEntryValue(mPath);
    }

    @Override
    public ValueType getType() {
        return getValue().getType();
    }

    @Override
    public boolean isEmpty() {
        return getType() == ValueType.EMPTY;
    }

    @Override
    public byte[] getRaw(byte[] defaultValue) {
        return getValue().getRaw(defaultValue);
    }

    @Override
    public boolean getBoolean(boolean defaultValue) {
        return getValue().getBoolean(defaultValue);
    }

    @Override
    public int getInt(int defaultValue) {
        return getValue().getInt(defaultValue);
    }

    @Override
    public double getDouble(double defaultValue) {
        return getValue().getDouble(defaultValue);
    }

    @Override
    public String getString(String defaultValue) {
        return getValue().getString(defaultValue);
    }

    @Override
    public void clearValue() {
        Storage storage = getStorage();
        storage.clearEntryValue(mPath);
    }

    @Override
    public void setRaw(byte[] value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.RAW, value));
    }

    @Override
    public void setBoolean(boolean value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.BOOLEAN, value));
    }

    @Override
    public void setInt(int value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.INT, value));
    }

    @Override
    public void setDouble(double value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.DOUBLE, value));
    }

    @Override
    public void setString(String value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.STRING, value));
    }

    private Storage getStorage() {
        Storage storage = mStorage.get();
        if (storage == null) {
            throw new IllegalStateException("storage was garbage collected");
        }

        return storage;
    }
}
