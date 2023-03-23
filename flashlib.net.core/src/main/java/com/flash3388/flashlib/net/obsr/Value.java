package com.flash3388.flashlib.net.obsr;

import java.util.Objects;

public class Value {

    private final ValueType mType;
    private final Object mValue;

    public Value() {
        mType = ValueType.EMPTY;
        mValue = null;
    }

    public Value(ValueType type, Object value) {
        Objects.requireNonNull(type, "type should not be null");
        ensureTypeValid(type, value);

        mType = type;
        mValue = value;
    }

    /**
     * Gets the current type of the value.
     *
     * @return type of value
     */
    public ValueType getType() {
        return mType;
    }

    /**
     * Gets whether the value is empty, meaning it has no value. This is represented
     * by the {@link #getType()} being {@link ValueType#EMPTY}.
     *
     * @return <b>true</b> if empty, <b>false</b> otherwise
     */
    public boolean isEmpty() {
        return mType == ValueType.EMPTY;
    }

    /**
     * Gets the typed value stored.
     *
     * This method will return the value if {@link #getType()} is {@link ValueType#RAW}.
     *
     * @param defaultValue value to return if the value is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    public byte[] getRaw(byte[] defaultValue) {
        return getValue(ValueType.RAW, byte[].class, defaultValue);
    }

    /**
     * Gets the typed value stored.
     *
     * This method will return the value if {@link #getType()} is {@link ValueType#BOOLEAN}.
     *
     * @param defaultValue value to return if the value is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    public boolean getBoolean(boolean defaultValue) {
        return getValue(ValueType.BOOLEAN, Boolean.class, defaultValue);
    }

    /**
     * Gets the typed value stored.
     *
     * This method will return the value if {@link #getType()} is {@link ValueType#INT}.
     *
     * @param defaultValue value to return if the value is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    public int getInt(int defaultValue) {
        return getValue(ValueType.INT, Integer.class, defaultValue);
    }

    /**
     * Gets the typed value stored.
     *
     * This method will return the value if {@link #getType()} is {@link ValueType#LONG}.
     *
     * @param defaultValue value to return if the value is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    public long getLong(long defaultValue) {
        return getValue(ValueType.LONG, Long.class, defaultValue);
    }

    /**
     * Gets the typed value stored.
     *
     * This method will return the value if {@link #getType()} is {@link ValueType#DOUBLE}.
     *
     * @param defaultValue value to return if the value is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    public double getDouble(double defaultValue) {
        return getValue(ValueType.DOUBLE, Double.class, defaultValue);
    }

    /**
     * Gets the typed value stored.
     *
     * This method will return the value if {@link #getType()} is {@link ValueType#STRING}.
     *
     * @param defaultValue value to return if the value is empty, i.e. {@link #isEmpty()} is <b>true</b>.
     * @return value stored, or <em>defaultValue</em> if empty.
     */
    public String getString(String defaultValue) {
        return getValue(ValueType.STRING, String.class, defaultValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        return mType == value.mType && Objects.equals(mValue, value.mValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mType, mValue);
    }

    @Override
    public String toString() {
        if (mType == ValueType.EMPTY) {
            return "{EMPTY}";
        }

        return String.format("{type=%s value=%s}", mType, mValue);
    }

    private <T> T getValue(ValueType wantedType, Class<T> typeCls, T defaultValue) {
        if (mType == ValueType.EMPTY) {
            return defaultValue;
        }

        if (mType != wantedType) {
            throw new GetTypeMismatchException(wantedType, mType);
        }

        return typeCls.cast(mValue);
    }

    private void ensureTypeValid(ValueType type, Object value) {
        switch (type) {
            case EMPTY:
                if (value != null) {
                    throw new SetTypeMismatchException(type, "expected null value, got " + value.getClass().getName());
                }
                break;
            case RAW:
                ensureType(type, byte[].class, value);
                break;
            case BOOLEAN:
                ensureType(type, Boolean.class, value);
                break;
            case INT:
                ensureType(type, Integer.class, value);
                break;
            case LONG:
                ensureType(type, Long.class, value);
                break;
            case DOUBLE:
                ensureType(type, Double.class, value);
                break;
            case STRING:
                ensureType(type, String.class, value);
                break;
            default:
                throw new IllegalArgumentException("unsupported type: " + type);
        }
    }

    private static <T> void ensureType(ValueType type, Class<?> expected, Object value) {
        if (!expected.isInstance(value)) {
            throw new SetTypeMismatchException(type, expected, value.getClass());
        }
    }
}