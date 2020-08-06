package com.flash3388.flashlib.robot.hid.generic;

public class GenericButton extends GenericButtonBase {

    private final RawHidInterface mInterface;
    private final int mChannel;
    private final int mButton;

    private boolean mIsInverted;

    public GenericButton(RawHidInterface anInterface, int channel, int button) {
        mInterface = anInterface;
        mChannel = channel;
        mButton = button;

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
        return mInterface.getButtonValue(mChannel, mButton) ^ mIsInverted;
    }
}
