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

    public static CompareResult forValue(int value) {
        for (CompareResult result : values()) {
            if (result.getValue() == value) {
                return result;
            }
        }

        throw new EnumConstantNotPresentException(CompareResult.class, String.valueOf(value));
    }
}
