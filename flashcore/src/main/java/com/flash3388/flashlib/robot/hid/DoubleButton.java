package com.flash3388.flashlib.robot.hid;

/**
 * An extension of {@link Button} which combines two button objects. This basically means that
 * {@link #isDown()} returns true if {@link #isDown()} for both buttons returns true.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public abstract class DoubleButton extends Button {
	
	private final Button mButton1;
	private final Button mButton2;
	
	/**
	 * Creates a dual button wrapper
	 * 
	 * @param button1 the first button
	 * @param button2 the second button
	 */
	public DoubleButton(Button button1, Button button2){
        mButton1 = button1;
		mButton2 = button2;
	}

	/**
	 * Gets the current button state
	 */
	@Override
	public boolean isDown() {
		return mButton1.isDown() && mButton2.isDown();
	}
}
