package com.flash3388.flashlib.robot.hid;

public class HidAxis implements Axis {

    private final HidInterface mHidInterface;
    private final int mChannel;
    private final int mAxis;

    private boolean mIsInverted;

    public HidAxis(HidInterface hidInterface, int channel, int axis) {
        mHidInterface = hidInterface;
        mChannel = channel;
        mAxis = axis;

        mIsInverted = false;
    }

    @Override
    public void setInverted(boolean isInverted) {
        mIsInverted = isInverted;
    }

    @Override
    public boolean isInverted() {
        return mIsInverted;
    }

    /**
     * Gets the value of the axis.
     *
     * @return the value of the axis
     */
    @Override
    public double get(){
        double raw = mHidInterface.getHidAxis(mChannel, mAxis);
        return mIsInverted ? -raw : raw;
    }

}
