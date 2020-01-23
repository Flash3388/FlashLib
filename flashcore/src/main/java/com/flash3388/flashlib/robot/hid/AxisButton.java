package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.time.Clock;

public class AxisButton extends HardwareButton {

    private final Axis mAxis;
    private final double mValuePressed;

    private boolean mIsInverted;

    public AxisButton(Clock clock, Axis axis, double valuePressed) {
        super(clock);

        mAxis = axis;
        mValuePressed = valuePressed;

        mIsInverted = false;
    }

    @Override
    public boolean isDown() {
        return (Math.abs(mAxis.getAsDouble()) > mValuePressed) ^ mIsInverted;
    }

    @Override
    public void setInverted(boolean inverted) {
        mIsInverted =  inverted;
    }

    @Override
    public boolean isInverted() {
        return mIsInverted;
    }
}
