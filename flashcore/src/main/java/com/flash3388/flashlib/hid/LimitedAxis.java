package com.flash3388.flashlib.hid;

public class LimitedAxis implements Axis {

    private final Axis mAxis;
    private final double mValueThreshold;
    private final double mMaxValue;

    public LimitedAxis(Axis axis, double valueThreshold, double maxValue) {
        mAxis = axis;
        mValueThreshold = valueThreshold;
        mMaxValue = maxValue;
    }

    @Override
    public double getAsDouble() {
        double value = mAxis.getAsDouble();
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

    @Override
    public Button asButton(double threshold, boolean isDirectional) {
        return mAxis.asButton(threshold, isDirectional);
    }
}
