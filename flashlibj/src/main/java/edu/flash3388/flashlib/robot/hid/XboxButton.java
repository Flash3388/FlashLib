package edu.flash3388.flashlib.robot.hid;

public enum XboxButton {
    A(0),
    B(1),
    X(2),
    Y(3),
    LB(4),
    RB(5),
    Back(6),
    Start(7),
    LeftStickButton(8),
    RightStickButton(9);

    private final int mButtonIndex;

    XboxButton(int buttonIndex) {
        mButtonIndex = buttonIndex;
    }

    public int buttonIndex() {
        return mButtonIndex;
    }
}
