package com.flash3388.flashlib.robot.systems.drive;

public class OmniDriveSpeed {

    private final double mFront;
    private final double mRight;
    private final double mBack;
    private final double mLeft;

    public OmniDriveSpeed(double front, double right, double back, double left) {
        mFront = front;
        mRight = right;
        mBack = back;
        mLeft = left;
    }

    public OmniDriveSpeed(double y, double x) {
        this(x, y, x, y);
    }

    public double getFront() {
        return mFront;
    }

    public double getRight() {
        return mRight;
    }

    public double getBack() {
        return mBack;
    }

    public double getLeft() {
        return mLeft;
    }
}
