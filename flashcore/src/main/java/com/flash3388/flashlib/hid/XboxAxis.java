package com.flash3388.flashlib.hid;

public enum XboxAxis {
    LeftStickX(0),
    LeftStickY(1),
    LT(2),
    RT(3),
    RightStickX(4),
    RightStickY(5);

    private final int mAxisIndex;

    XboxAxis(int axisIndex) {
        mAxisIndex = axisIndex;
    }

    public int axisIndex() {
        return mAxisIndex;
    }

    public static int count() {
        return values().length;
    }
}
