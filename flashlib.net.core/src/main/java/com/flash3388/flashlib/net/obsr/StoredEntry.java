package com.flash3388.flashlib.net.obsr;

/**
 * An entry represents a named information field in a {@link StoredObject}.
 * The entry can either be empty or have a <i>typed</i> value represented by {@link EntryValueType}.
 *
 * @since FlashLib 3.2.0
 */
public interface StoredEntry {

    /**
     * Gets the current type of the value stored by this entry.
     *
     * @return type of value
     */
    EntryValueType getType();

    /**
     * Gets whether the current entry is empty, meaning it has no value. This is represented
     * by the {@link #getType()} being {@link EntryValueType#EMPTY}.
     *
     * @return <b>true</b> if empty, <b>false</b> otherwise
     */
    boolean isEmpty();

    /**
     * Gets the typed value stored in this entry.
     *
     * This method will return the value if {@link #getType()} is {@link EntryValueType#RAW}.
     *
     * @param defaultValue value to return if the entry is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    byte[] getRaw(byte[] defaultValue);

    /**
     * Gets the typed value stored in this entry.
     *
     * This method will return the value if {@link #getType()} is {@link EntryValueType#BOOLEAN}.
     *
     * @param defaultValue value to return if the entry is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    boolean getBoolean(boolean defaultValue);

    /**
     * Gets the typed value stored in this entry.
     *
     * This method will return the value if {@link #getType()} is {@link EntryValueType#INT}.
     *
     * @param defaultValue value to return if the entry is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    int getInt(int defaultValue);

    /**
     * Gets the typed value stored in this entry.
     *
     * This method will return the value if {@link #getType()} is {@link EntryValueType#DOUBLE}.
     *
     * @param defaultValue value to return if the entry is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    double getDouble(double defaultValue);

    /**
     * Gets the typed value stored in this entry.
     *
     * This method will return the value if {@link #getType()} is {@link EntryValueType#STRING}.
     *
     * @param defaultValue value to return if the entry is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    String getString(String defaultValue);

    /**
     * Clears the current entry of any stored value.
     * After this call {@link #getType()} will be {@link EntryValueType#EMPTY}.
     */
    void clearValue();

    /**
     * Sets the value of this entry.
     * If the entry is empty, i.e. current type of the entry is {@link EntryValueType#EMPTY}, then
     * after this call, it will no longer be empty, changing it to {@link EntryValueType#RAW}.
     * If the entry is neither empty nor has a type of {@link EntryValueType#RAW} then this call will fail,
     * as changing types without clearing the value first is not possible.
     *
     * @param value value to set
     */
    void setRaw(byte[] value);
    /**
     * Sets the value of this entry.
     * If the entry is empty, i.e. current type of the entry is {@link EntryValueType#EMPTY}, then
     * after this call, it will no longer be empty, changing it to {@link EntryValueType#BOOLEAN}.
     * If the entry is neither empty nor has a type of {@link EntryValueType#BOOLEAN} then this call will fail,
     * as changing types without clearing the value first is not possible.
     *
     * @param value value to set
     */
    void setBoolean(boolean value);

    /**
     * Sets the value of this entry.
     * If the entry is empty, i.e. current type of the entry is {@link EntryValueType#EMPTY}, then
     * after this call, it will no longer be empty, changing it to {@link EntryValueType#INT}.
     * If the entry is neither empty nor has a type of {@link EntryValueType#INT} then this call will fail,
     * as changing types without clearing the value first is not possible.
     *
     * @param value value to set
     */
    void setInt(int value);

    /**
     * Sets the value of this entry.
     * If the entry is empty, i.e. current type of the entry is {@link EntryValueType#EMPTY}, then
     * after this call, it will no longer be empty, changing it to {@link EntryValueType#DOUBLE}.
     * If the entry is neither empty nor has a type of {@link EntryValueType#DOUBLE} then this call will fail,
     * as changing types without clearing the value first is not possible.
     *
     * @param value value to set
     */
    void setDouble(double value);

    /**
     * Sets the value of this entry.
     * If the entry is empty, i.e. current type of the entry is {@link EntryValueType#EMPTY}, then
     * after this call, it will no longer be empty, changing it to {@link EntryValueType#STRING}.
     * If the entry is neither empty nor has a type of {@link EntryValueType#STRING} then this call will fail,
     * as changing types without clearing the value first is not possible.
     *
     * @param value value to set
     */
    void setString(String value);
}
