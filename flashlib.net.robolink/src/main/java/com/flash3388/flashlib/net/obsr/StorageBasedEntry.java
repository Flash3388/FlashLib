package com.flash3388.flashlib.net.obsr;

import java.lang.ref.WeakReference;
import java.util.Objects;

class StorageBasedEntry implements StoredEntry {

    private final StoragePath mPath;
    private final WeakReference<Storage> mStorage;

    public StorageBasedEntry(StoragePath path, Storage storage) {
        mPath = path;
        mStorage = new WeakReference<>(storage);
    }

    public StoragePath getPath() {
        return mPath;
    }

    @Override
    public ValueProperty valueProperty() {
        Storage storage = getStorage();
        return storage.getEntryValueProperty(mPath);
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
    public long getLong(long defaultValue) {
        return getValue().getLong(defaultValue);
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
    public boolean[] getBooleanArray(boolean[] defaultValue) {
        return getValue().getBooleanArray(defaultValue);
    }

    @Override
    public int[] getIntArray(int[] defaultValue) {
        return getValue().getIntArray(defaultValue);
    }

    @Override
    public double[] getDoubleArray(double[] defaultValue) {
        return getValue().getDoubleArray(defaultValue);
    }

    @Override
    public String[] getStringArray(String[] defaultValue) {
        return getValue().getStringArray(defaultValue);
    }

    @Override
    public void clearValue() {
        Storage storage = getStorage();
        storage.clearEntryValue(mPath);
    }

    @Override
    public void delete() {
        Storage storage = getStorage();
        storage.deleteEntry(mPath);
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
    public void setLong(long value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.LONG, value));
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

    @Override
    public void setBooleanArray(boolean[] value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.BOOLEAN_ARRAY, value));
    }

    @Override
    public void setIntArray(int[] value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.INT_ARRAY, value));
    }

    @Override
    public void setDoubleArray(double[] value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.DOUBLE_ARRAY, value));
    }

    @Override
    public void setStringArray(String[] value) {
        Storage storage = getStorage();
        storage.setEntryValue(mPath, new Value(ValueType.STRING_ARRAY, value));
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
}
