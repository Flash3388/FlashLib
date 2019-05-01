package com.flash3388.flashlib.robot.hid;

public class AxisButton extends Button {

    private final Axis mAxis;
    private final double mValuePressed;

    private boolean mIsInverted;

    public AxisButton(Axis axis, double valuePressed) {
        mAxis = axis;
        mValuePressed = valuePressed;

        mIsInverted = false;
    }

    @Override
    public boolean isDown() {
        return Math.abs(mAxis.get() - mValuePressed) > 0 ^ mIsInverted;
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
