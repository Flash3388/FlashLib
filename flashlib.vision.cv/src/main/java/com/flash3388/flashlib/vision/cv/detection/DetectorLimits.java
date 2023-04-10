package com.flash3388.flashlib.vision.cv.detection;

public class DetectorLimits {

    private final double mMinAcceptableScore;
    private final double mMinAcceptableSizePixels;

    public DetectorLimits(double minAcceptableScore, double minAcceptableSizePixels) {
        mMinAcceptableScore = minAcceptableScore;
        mMinAcceptableSizePixels = minAcceptableSizePixels;
    }

    public double getMinAcceptableScore() {
        return mMinAcceptableScore;
    }

    public double getMinAcceptableSizePixels() {
        return mMinAcceptableSizePixels;
    }
}
