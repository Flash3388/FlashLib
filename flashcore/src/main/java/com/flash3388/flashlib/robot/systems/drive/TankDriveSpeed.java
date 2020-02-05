package com.flash3388.flashlib.robot.systems.drive;

public class TankDriveSpeed {

    private final double mRight;
    private final double mLeft;

    public TankDriveSpeed(double right, double left) {
        mRight = right;
        mLeft = left;
    }

    public double getRight() {
        return mRight;
    }

    public double getLeft() {
        return mLeft;
    }
}
