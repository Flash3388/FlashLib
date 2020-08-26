package com.flash3388.flashlib.vision.cv.template;

import org.opencv.core.Point;

public class TemplateMatchingResult implements Comparable<TemplateMatchingResult> {

    private final Point mCenterPoint;
    private final double mScore;

    public TemplateMatchingResult(Point centerPoint, double score) {
        mCenterPoint = centerPoint;
        mScore = score;
    }

    public Point getCenterPoint() {
        return mCenterPoint;
    }

    public double getScore() {
        return mScore;
    }

    @Override
    public int compareTo(TemplateMatchingResult o) {
        if (mScore == o.mScore) {
            return 0;
        }

        return mScore < o.mScore ? -1 : 1;
    }
}
