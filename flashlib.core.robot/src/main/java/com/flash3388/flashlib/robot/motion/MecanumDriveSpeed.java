package com.flash3388.flashlib.robot.motion;

public class MecanumDriveSpeed {

    private final double mFrontRight;
    private final double mBackRight;
    private final double mFrontLeft;
    private final double mBackLeft;

    public MecanumDriveSpeed(double frontRight, double backRight, double frontLeft, double backLeft) {
        mFrontRight = frontRight;
        mBackRight = backRight;
        mFrontLeft = frontLeft;
        mBackLeft = backLeft;
    }

    public double getFrontRight() {
        return mFrontRight;
    }

    public double getBackRight() {
        return mBackRight;
    }

    public double getFrontLeft() {
        return mFrontLeft;
    }

    public double getBackLeft() {
        return mBackLeft;
    }
}
