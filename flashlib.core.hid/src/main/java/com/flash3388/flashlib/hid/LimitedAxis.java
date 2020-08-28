package com.flash3388.flashlib.hid;

/**
 * A wrapping implementation of {@link Axis} which limits the values
 * of a given axis object, as defined by {@link Axis#limit(double, double)}.
 * All axis calls are delegated to the wrapped axis.
 *
 * @since FlashLib 3.0.0
 */
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
    public Axis limit(double valueThreshold, double maxValue) {
        return mAxis.limit(valueThreshold, maxValue);
    }

    @Override
    public Button asButton(double threshold, boolean isDirectional) {
        return mAxis.asButton(threshold, isDirectional);
    }
}
