package edu.flash3388.flashlib.robot.hid;

/**
 * Representing a button of a POV such as a D-Pad.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class POVButton extends Button {

    private final HIDInterface mHidInterface;
    private final int mChannel;
    private final int mPov;
	private final POVRange mRange;

	public POVButton(HIDInterface hidInterface, int channel, int pov, POVRange povRange) {
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
