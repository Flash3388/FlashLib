package com.flash3388.flashlib.robot.hid.generic;

public class AxisButton extends GenericButtonBase {

    private final RawHidInterface mInterface;
    private final int mChannel;
    private final int mAxis;
    private final double mThreshold;

    private boolean mIsInverted;

    public AxisButton(RawHidInterface anInterface, int channel, int axis, double threshold) {
        mInterface = anInterface;
        mChannel = channel;
        mAxis = axis;
        mThreshold = threshold;

        schedule(this);

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
        boolean value = Math.abs(mInterface.getAxisValue(mChannel, mAxis)) > mThreshold;
        return value ^ mIsInverted;
    }
}
