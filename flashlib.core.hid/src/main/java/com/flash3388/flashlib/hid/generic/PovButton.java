package com.flash3388.flashlib.hid.generic;

public class PovButton extends GenericButtonBase {

    private final RawHidInterface mInterface;
    private final int mChannel;
    private final int mPov;

    private final PovRange mRange;

    private boolean mIsInverted;

    public PovButton(RawHidInterface anInterface, int channel, int pov, PovRange range) {
        mInterface = anInterface;
        mChannel = channel;
        mPov = pov;
        mRange = range;
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
    public boolean getAsBoolean() {
        return mRange.isInRange(mInterface.getPovValue(mChannel, mPov)) ^ mIsInverted;
    }
}
