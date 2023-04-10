package com.flash3388.flashlib.robot.motion;

import com.jmath.vectors.Vector2;

public class HolonomicDriveSpeed {

    private final Vector2 mDriveVector;
    private final double mRotation;

    public HolonomicDriveSpeed(Vector2 driveVector, double rotation) {
        mDriveVector = driveVector;
        mRotation = rotation;
    }

    public Vector2 getDriveVector() {
        return mDriveVector;
    }

    public double getRotation() {
        return mRotation;
    }
}
