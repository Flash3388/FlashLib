package com.flash3388.flashlib.robot.systems.drive;

public class ArcadeDriveSpeed {

    private final double mMove;
    private final double mRotate;

    public ArcadeDriveSpeed(double move, double rotate) {
        mMove = move;
        mRotate = rotate;
    }

    public double getMove() {
        return mMove;
    }

    public double getRotate() {
        return mRotate;
    }
}
