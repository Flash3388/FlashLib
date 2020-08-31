package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Axis;

public class AxisButton extends GenericButtonBase {

    private final Axis mAxis;
    private final double mThreshold;
    private final boolean mIsDirectional;

    private boolean mIsInverted;

    public AxisButton(Axis axis, double threshold, boolean isDirectional) {
        mAxis = axis;
        mThreshold = threshold;
        mIsDirectional = isDirectional;

        scheduleAutoUpdate(this);

        mIsInverted = false;
    }

    @Override
    public void setInverted(boolean inverted) {
        mIsInverted = true;
    }

    @Override
    public boolean isInverted() {
        return mIsInverted;
    }

    @Override
    public boolean getAsBoolean() {
        double axisValue = mAxis.getAsDouble();
        boolean value = Math.abs(axisValue) > Math.abs(mThreshold);

        if (mIsDirectional) {
            value &= Math.signum(axisValue) == Math.signum(mThreshold);
        }

        return value ^ mIsInverted;
    }
}
