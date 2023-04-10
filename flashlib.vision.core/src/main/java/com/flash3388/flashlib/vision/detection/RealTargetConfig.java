package com.flash3388.flashlib.vision.detection;

public class RealTargetConfig {

    private final double mWidthCm;
    private final double mHeightCm;

    public RealTargetConfig(double widthCm, double heightCm) {
        mWidthCm = widthCm;
        mHeightCm = heightCm;
    }

    public double getWidthCm() {
        return mWidthCm;
    }

    public double getHeightCm() {
        return mHeightCm;
    }

    public double getDimensionsRatio() {
        return mWidthCm / mHeightCm;
    }
}
