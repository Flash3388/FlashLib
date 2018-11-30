package edu.flash3388.flashlib.robot.systems.drive;

public class MecanumDriveSpeed {

    private final double mFrontRight;
    private final double mRearRight;
    private final double mFrontLeft;
    private final double mRearLeft;

    public MecanumDriveSpeed(double frontRight, double rearRight, double frontLeft, double rearLeft) {
        mFrontRight = frontRight;
        mRearRight = rearRight;
        mFrontLeft = frontLeft;
        mRearLeft = rearLeft;
    }

    public double getFrontRight() {
        return mFrontRight;
    }

    public double getRearRight() {
        return mRearRight;
    }

    public double getFrontLeft() {
        return mFrontLeft;
    }

    public double getRearLeft() {
        return mRearLeft;
    }
}
