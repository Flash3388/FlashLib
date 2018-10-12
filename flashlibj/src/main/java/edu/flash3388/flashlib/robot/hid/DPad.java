package edu.flash3388.flashlib.robot.hid;

/**
 * Represents a D-Pad from and XBox controller.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class DPad extends POV {

	private final Button mUp;
    private final Button mDown;
    private final Button mRight;
    private final Button mLeft;
    private final Button mFull;

	public DPad(HIDInterface hidInterface, int channel, int pov){
		super(hidInterface, channel, pov);
		
		mUp = new POVButton(hidInterface, channel, pov, POVRange.UP);
		mDown = new POVButton(hidInterface, channel, pov, POVRange.DOWN);
		mRight = new POVButton(hidInterface, channel, pov, POVRange.RIGHT);
		mLeft = new POVButton(hidInterface, channel, pov, POVRange.LEFT);
		mFull = new POVButton(hidInterface, channel, pov, POVRange.FULL);
	}

	/**
	 * Gets the up DPad button object
	 * @return up button
	 */
	public Button getUp(){
		return mUp;
	}

	/**
	 * Gets the down DPad button object
	 * @return down button
	 */
	public Button getDown(){
		return mDown;
	}

	/**
	 * Gets the right DPad button object
	 * @return right button
	 */
	public Button getRight(){
		return mRight;
	}

	/**
	 * Gets the left DPad button object
	 * @return left button
	 */
	public Button getLeft(){
		return mLeft;
	}

	public Button getFull() {
		return mFull;
	}
}
