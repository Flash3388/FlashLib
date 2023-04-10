package com.flash3388.flashlib.vision.detection;

public class CameraConfig {

    private final double mFovRadians;

    public CameraConfig(double fovRadians) {
        mFovRadians = fovRadians;
    }

    public double getFovRadians() {
        return mFovRadians;
    }
}
