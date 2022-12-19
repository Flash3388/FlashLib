package com.flash3388.flashlib.net.obsr;

public interface StoredEntry {

    EntryType getType();
    boolean isEmpty();

    byte[] getRaw(byte[] defaultValue);
    boolean getBoolean(boolean defaultValue);
    int getInt(int defaultValue);
    double getDouble(double defaultValue);
    String getString(String defaultValue);

    void clearValue();
    void setRaw(byte[] value);
    void setBoolean(boolean value);
    void setInt(int value);
    void setDouble(double value);
    void setString(String value);
}
