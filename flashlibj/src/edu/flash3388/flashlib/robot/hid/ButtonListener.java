package edu.flash3388.flashlib.robot.hid;

/**
 * Listener for button events
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface ButtonListener {
	/**
	 * Called when the button is pressed
	 * @param e event data
	 */
	void onPress(ButtonEvent e);
	/**
	 * Called when the button is held
	 * @param e event data
	 */
	void onHold(ButtonEvent e);
	/**
	 * Called when the button is released
	 * @param e event data
	 */
	void onRelease(ButtonEvent e);
}
