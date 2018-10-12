package edu.flash3388.flashlib.robot.hid;

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

	public PovButton(HidInterface hidInterface, int channel, int pov, PovRange povRange) {
        mHidInterface = hidInterface;
        mChannel = channel;
        mPov = pov;
		mRange = povRange;
	}
	
	/**
	 * Gets the current button state
	 */
	@Override
	public boolean isDown() {
		return mRange.isInRange(mHidInterface.getHidPov(mChannel, mPov));
	}
}
