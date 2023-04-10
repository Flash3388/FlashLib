package com.flash3388.flashlib.vision.color;

public class DimensionRange {

    private final ColorDimension mDimension;
    private final int mMin;
    private final int mMax;

    public DimensionRange(ColorDimension dimension, int min, int max) {
        mDimension = dimension;
        mMin = min;
        mMax = max;
    }

    public ColorDimension getDimension() {
        return mDimension;
    }

    public int getMin() {
        return mMin;
    }

    public int getMax() {
        return mMax;
    }
}
