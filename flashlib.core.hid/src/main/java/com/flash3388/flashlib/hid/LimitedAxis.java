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

    /**
     * {@inheritDoc}
     * <p>
     *     The returned value is modified from the wrapped axis's {@link Axis#getAsDouble()}
     *     by the defined limitation values. The following limits are applied:
     * </p>
     * <ul>
     *     <li>Threshold limit sets the minimum absolute value an axis can produce. If the actual value is bellow
     *     that threshold, <code>0</code> is returned.</li>
     *     <li>Max value limit sets the largest absolute value an axis can produce. If the actual value is above that,
     *     <code>maxValue</code> is returned instead.</li>
     * </ul>
     */
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

    /**
     * {@inheritDoc}
     * <p>
     *     Delegates to the wrapped axis's {@link Axis#setInverted(boolean)}
     * </p>
     */
    @Override
    public void setInverted(boolean inverted) {
        mAxis.setInverted(inverted);
    }

    /**
     * {@inheritDoc}
     * <p>
     *     Delegates to the wrapped axis's {@link Axis#isInverted()}
     * </p>
     */
    @Override
    public boolean isInverted() {
        return mAxis.isInverted();
    }

    /**
     * {@inheritDoc}
     * <p>
     *     Delegates to the wrapped axis's {@link Axis#limit(double, double)}
     * </p>
     */
    @Override
    public Axis limit(double valueThreshold, double maxValue) {
        return mAxis.limit(valueThreshold, maxValue);
    }

    /**
     * {@inheritDoc}
     * <p>
     *     Delegates to the wrapped axis's {@link Axis#asButton(double, boolean)}
     * </p>
     */
    @Override
    public Button asButton(double threshold, boolean isDirectional) {
        return mAxis.asButton(threshold, isDirectional);
    }
}
