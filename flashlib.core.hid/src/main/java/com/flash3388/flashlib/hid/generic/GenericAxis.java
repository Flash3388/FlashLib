package com.flash3388.flashlib.hid.generic;

import com.flash3388.flashlib.hid.Axis;
import com.flash3388.flashlib.hid.Button;

public class GenericAxis implements Axis {

    private final RawHidInterface mInterface;
    private final int mChannel;
    private final int mAxis;

    private boolean mIsInverted;

    public GenericAxis(RawHidInterface anInterface, int channel, int axis) {
        mInterface = anInterface;
        mChannel = channel;
        mAxis = axis;

        mIsInverted = false;
    }

    @Override
    public Button asButton(double threshold, boolean isDirectional) {
        return new AxisButton(this, threshold, isDirectional);
    }

    @Override
    public void setInverted(boolean inverted) {
        mIsInverted = inverted;
    }

    @Override
    public boolean isInverted() {
        return mIsInverted;
    }

    @Override
    public double getAsDouble() {
        double value = mInterface.getAxisValue(mChannel, mAxis);
        return mIsInverted ? -value : value;
    }
}
