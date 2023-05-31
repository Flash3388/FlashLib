package com.flash3388.flashlib.vision.color;

public class DimensionRange {

    private final ColorDimension mDimension;
    private final int mMin;
    private final int mMax;
    private final boolean mInverted;

    public DimensionRange(ColorDimension dimension, int min, int max, boolean inverted) {
        mDimension = dimension;
        mMin = min;
        mMax = max;
        mInverted = inverted;
    }

    public DimensionRange(ColorDimension dimension, int min, int max) {
        mDimension = dimension;
        mMin = min;
        mMax = max;
        mInverted = false;
    }
    public ColorDimension getDimension() {
        return mDimension;
    }

    public boolean isInverted() {
        return mInverted;
    }

    public int getMin() {
        return mMin;
    }

    public int getMax() {
        return mMax;
    }
}
