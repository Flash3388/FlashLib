package com.flash3388.flashlib.vision.cv.processing.disFolderIsNasty;

public class PhysicalVisionConfig {
    private final double realTargetWidth;
    private final double camFovRadians;

    public PhysicalVisionConfig(double realTargetWidth, double camFovRadians) {
        this.realTargetWidth = realTargetWidth;
        this.camFovRadians = camFovRadians;
    }

    public double getTargetWidth() {
        return realTargetWidth;
    }

    public double getCamFovRadians() {
        return camFovRadians;
    }
}
