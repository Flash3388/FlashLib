package com.flash3388.flashlib.util;

public enum CompareResult {
    GREATER_THAN(1),
    SMALLER_THAN(-1),
    EQUAL_TO(0);

    private final int mValue;

    CompareResult(int value) {
        mValue = value;
    }

    public int value() {
        return mValue;
    }

    public boolean is(int value) {
        return mValue == value;
    }

    public static CompareResult forValue(int value) {
        for (CompareResult result : values()) {
            if (result.value() == value) {
                return result;
            }
        }

        throw new EnumConstantNotPresentException(CompareResult.class, String.valueOf(value));
    }
    
    public static boolean in(int value, CompareResult... possibleResults) {
        for (CompareResult compareResult : possibleResults) {
            if (compareResult.is(value)) {
                return true;
            }
        }

        return false;
    }
}
