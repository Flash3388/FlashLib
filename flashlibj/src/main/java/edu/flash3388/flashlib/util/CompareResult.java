package edu.flash3388.flashlib.util;

public enum CompareResult {
    GREATER_THAN(1),
    SMALLER_THAN(-1),
    EQUAL_TO(0);

    private final int mValue;

    CompareResult(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }
}
