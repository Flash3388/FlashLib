package com.flash3388.flashlib.robot.hid;

/**
 * Representing a button of a Pov such as a D-Pad.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class PovButton extends Button {

    private final HidInterface mHidInterface;
    private final int mChannel;
    private final int mPov;

	private final PovRange mRange;

	private boolean mIsInverted;

	public PovButton(HidInterface hidInterface, int channel, int pov, PovRange povRange) {
        mHidInterface = hidInterface;
        mChannel = channel;
        mPov = pov;
		mRange = povRange;

		mIsInverted = false;
	}
	
	/**
	 * Gets the current button state
	 */
	@Override
	public boolean isDown() {
		return mRange.isInRange(mHidInterface.getHidPov(mChannel, mPov)) ^ mIsInverted;
	}

    @Override
    public void setInverted(boolean inverted) {
        mIsInverted = inverted;
    }

    @Override
    public boolean isInverted() {
        return mIsInverted;
    }
}
