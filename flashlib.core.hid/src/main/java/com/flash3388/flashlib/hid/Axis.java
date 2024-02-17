package com.flash3388.flashlib.hid;

import com.flash3388.flashlib.control.Invertable;

import java.util.function.DoubleSupplier;

/**
 * Represents an axis on a human interface device.
 * <p>
 *     An axis is analog input data, with a value normally ranging between -1 and 1,
 *     as percentages for power and sign for direction. For example: -0.3 indicates a 30%, at a negative direction.
 *     The direction itself, whether it is forward, or backward on the axis does change between axes (like Xbox vs
 *     Joystick). It is possible to use {@link #setInverted(boolean)} to remedy that if needed.
 * </p>
 * <p>
 *     Some special axes may produce a half range (-1 -&gt; 0 or 0 -&gt; 1), like
 *     the Xbox's Right and Left Triggers. Those are considered half-axes, but
 *     are still represented by this class.
 * </p>
 *
 * @since FlashLib 3.0.0
 */
public interface Axis extends DoubleSupplier, Invertable {

    /**
     * <p>
     *     Gets the value from the axis, normally ranging between -1 and 1 as an indication of
     *     power in percentages (0.3 = 30%) and direction (-0.3 = backward, 0.3 = forward).
     * </p>
     * <p>
     *     If {@link #isInverted()} is <b>true</b>, the direction is reversed, i.e.
     *     <code>-0.5 -&gt; 0.5</code>.
     * </p>
     */
    @Override
    double getAsDouble();

    /**
     * {@inheritDoc}
     * <p>
     *     If inverted <b>true</b>: any values from {@link #getAsDouble()} will be reversed in sign,
     *     i.e. <code>-0.5 -&gt; 0.5</code>.
     * </p>
     */
    @Override
    void setInverted(boolean inverted);

    /**
     * Returns a limited version of this {@link Axis}. This new version limits the values returned by the axis.
     * There are two types of limits:
     * <ul>
     *     <li>Threshold indicates the minimum absolute value an axis can produce. If the actual value is bellow
     *     that threshold, <code>0</code> is returned.</li>
     *     <li>Max value indicates the largest absolute value an axis can produce. If the actual value is above that,
     *     <code>maxValue</code> is returned instead.</li>
     * </ul>
     *
     * @param valueThreshold minimum absolute value to produce from the axis.
     * @param maxValue maximum absolute value to produce from the axis.
     *
     * @return a new {@link Axis}, based on this one, with limited values.
     */
    default Axis limit(double valueThreshold, double maxValue) {
        return new LimitedAxis(this, valueThreshold, maxValue);
    }

    /**
     * Gets a {@link Button} representation of this {@link Axis}. The button is considered activated
     * (i.e. <b>true</b>) if:
     * <ul>
     *     <li>If isDirectional <b>true</b>:
     *     <code>|axisValue| &gt; |threshold| and signum(axisValue) == signum(threshold)</code></li>
     *     <li>If isDirectional <b>false</b> <code>|axisValue| &gt; |threshold|</code></li>
     * </ul>
     *
     * @param threshold minimum value for the axis for the button to be considered <em>activated</em>.
     * @param isDirectional whether the threshold is absolute.
     *
     * @return a {@link Button} based on this axis.
     */
    Button asButton(double threshold, boolean isDirectional);
}
