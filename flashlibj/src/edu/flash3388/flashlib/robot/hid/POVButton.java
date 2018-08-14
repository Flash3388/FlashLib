package edu.flash3388.flashlib.robot.hid;

/**
 * Representing a button of a POV such as a D-Pad.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class POVButton extends HIDButton {

	private final POVRange mRange;
	
	/**
	 * Creates a new instance of POVButton. The created button is configured to the given type.
	 * 
	 * @param hid the HID
	 * @param num the pov number
	 * @param povRange the range
	 */
	public POVButton(HID hid, int num, POVRange povRange) {
		super(hid, num);
		mRange = povRange;
	}
	
	/**
	 * Gets the current button state
	 */
	@Override
	public boolean isDown() {
		return mRange.isInRange(getHID().getRawPov(getButtonNumber()));
	}
}
