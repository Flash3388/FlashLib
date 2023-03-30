package com.flash3388.flashlib.net.obsr;

import java.lang.ref.WeakReference;
import java.util.Objects;

public class StorageBasedEntry implements StoredEntry {

    private final StoragePath mPath;
    private final WeakReference<Storage> mStorage;
    private boolean mIsDeleted;

    public StorageBasedEntry(StoragePath path, Storage storage) {
        mPath = path;
        mStorage = new WeakReference<>(storage);
        mIsDeleted = false;
    }

    public StoragePath getPath() {
        return mPath;
    }

    @Override
    public ValueProperty valueProperty() {
        checkDeleted();
        Storage storage = getStorage();
        return storage.getEntryValueProperty(mPath);
    }

    @Override
    public Value getValue() {
        checkDeleted();
        Storage storage = getStorage();
        return storage.getEntryValue(mPath);
    }

    @Override
    public ValueType getType() {
        checkDeleted();
        return getValue().getType();
    }

    @Override
    public boolean isEmpty() {
        checkDeleted();
        return getType() == ValueType.EMPTY;
    }

    @Override
    public byte[] getRaw(byte[] defaultValue) {
        checkDeleted();
        return getValue().getRaw(defaultValue);
    }

    @Override
    public boolean getBoolean(boolean defaultValue) {
        checkDeleted();
        return getValue().getBoolean(defaultValue);
    }

    @Override
    public int getInt(int defaultValue) {
        checkDeleted();
        return getValue().getInt(defaultValue);
    }

    @Override
    public long getLong(long defaultValue) {
        checkDeleted();
        return getValue().getLong(defaultValue);
    }

    @Override
    public double getDouble(double defaultValue) {
        checkDeleted();
        return getValue().getDouble(defaultValue);
    }

    @Override
    public String getString(String defaultValue) {
        checkDeleted();
        return getValue().getString(defaultValue);
    }

    @Override
    public void clearValue() {
        checkDeleted();
        Storage storage = getStorage();
        storage.clearEntryValue(mPath);
    }

    @Override
    public void delete() {
        checkDeleted();
        Storage storage = getStorage();
        storage.deleteEntry(mPath);
    }

    @Override
    public void setRaw(byte[] value) {
        checkDeleted();
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.RAW, value));
    }

    @Override
    public void setBoolean(boolean value) {
        checkDeleted();
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.BOOLEAN, value));
    }

    @Override
    public void setInt(int value) {
        checkDeleted();
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.INT, value));
    }

    @Override
    public void setLong(long value) {
        checkDeleted();
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.LONG, value));
    }

    @Override
    public void setDouble(double value) {
        checkDeleted();
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.DOUBLE, value));
    }

    @Override
    public void setString(String value) {
        checkDeleted();
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.STRING, value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageBasedEntry that = (StorageBasedEntry) o;
        return Objects.equals(mPath, that.mPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mPath);
    }

    @Override
    public String toString() {
        return String.format("{ENTRY %s}", mPath);
    }

    private Storage getStorage() {
        Storage storage = mStorage.get();
        if (storage == null) {
            throw new IllegalStateException("storage was garbage collected");
        }

        return storage;
    }

    private void checkDeleted() {
        if (mIsDeleted) {
            throw new IllegalStateException("entry is deleted");
        }
    }
}
