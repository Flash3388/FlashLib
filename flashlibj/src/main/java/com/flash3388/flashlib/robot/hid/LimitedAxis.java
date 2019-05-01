package com.flash3388.flashlib.robot.hid;

import com.jmath.ExtendedMath;

public class LimitedAxis implements Axis {

    private final Axis mAxis;
    private double mValueThreshold;
    private double mMaxValue;

    public LimitedAxis(Axis axis, double valueThreshold, double maxValue) {
        mAxis = axis;
        mValueThreshold = valueThreshold;
        mMaxValue = maxValue;
    }

    public LimitedAxis(Axis axis) {
        this(axis, 0.0, 1.0);
    }

    public void setValueThreshold(double valueThreshold) {
        if (!ExtendedMath.constrained(valueThreshold, 0.0, 1.0)) {
            throw new IllegalArgumentException("threshold [0.0->1.0]");
        }

        mValueThreshold = valueThreshold;
    }

    public double getValueThreshold() {
        return mValueThreshold;
    }

    public void setMaxValue(double maxValue) {
        if (!ExtendedMath.constrained(maxValue, 0.0, 1.0)) {
            throw new IllegalArgumentException("max [0.0->1.0]");
        }

        mMaxValue = maxValue;
    }

    public double getMaxValue() {
        return mMaxValue;
    }

    @Override
    public double get() {
        double value = mAxis.get();
        if (Math.abs(value) < mValueThreshold) {
            return 0;
        }
        if (Math.abs(value) > mMaxValue) {
            return mMaxValue * Math.signum(value);
        }

        return value;
    }

    @Override
    public void setInverted(boolean inverted) {
        mAxis.setInverted(inverted);
    }

    @Override
    public boolean isInverted() {
        return mAxis.isInverted();
    }
}
