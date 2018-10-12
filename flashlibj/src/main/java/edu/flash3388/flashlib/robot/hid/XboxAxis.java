package edu.flash3388.flashlib.robot.hid;

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
}
