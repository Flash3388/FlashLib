package com.flash3388.flashlib.robot.motion;

public enum Direction {
    FORWARD(true),
    BACKWARD(false);

    private final boolean mBooleanValue;

    Direction(boolean booleanValue) {
        mBooleanValue = booleanValue;
    }

    public int sign() {
        return mBooleanValue ? 1 : -1;
    }

    public boolean booleanValue() {
        return mBooleanValue;
    }
}
