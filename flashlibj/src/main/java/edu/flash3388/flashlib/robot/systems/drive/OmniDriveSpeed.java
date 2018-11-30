package edu.flash3388.flashlib.robot.systems.drive;

public class OmniDriveSpeed {

    private final double mFront;
    private final double mRight;
    private final double mRear;
    private final double mLeft;

    public OmniDriveSpeed(double front, double right, double rear, double left) {
        mFront = front;
        mRight = right;
        mRear = rear;
        mLeft = left;
    }

    public double getFront() {
        return mFront;
    }

    public double getRight() {
        return mRight;
    }

    public double getRear() {
        return mRear;
    }

    public double getLeft() {
        return mLeft;
    }
}
