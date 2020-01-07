package com.flash3388.flashlib.robot.hid;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;

/**
 * Representing a button of a Pov such as a D-Pad.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class PovButton extends HardwareButton {

    private final HidInterface mHidInterface;
    private final int mChannel;
    private final int mPov;

	private final PovRange mRange;

	private boolean mIsInverted;

	public PovButton(Clock clock, Time maxPressTime, HidInterface hidInterface, int channel, int pov, PovRange povRange) {
	    super(clock, maxPressTime);

        mHidInterface = hidInterface;
        mChannel = channel;
        mPov = pov;
		mRange = povRange;

		mIsInverted = false;
	}

	public PovButton(Clock clock, HidInterface hidInterface, int channel, int pov, PovRange povRange) {
	    this(clock, DEFAULT_MAX_PRESS_TIME, hidInterface, channel, pov, povRange);
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
