package com.flash3388.flashlib.vision.cv.template;

import org.opencv.core.Point;

public class ScaledTemplateMatchingResult extends TemplateMatchingResult {

    private final double mScaleFactor;

    public ScaledTemplateMatchingResult(Point centerPoint, double score, double scaleFactor) {
        super(centerPoint, score);
        mScaleFactor = scaleFactor;
    }

    public double getScaleFactor() {
        return mScaleFactor;
    }
}
