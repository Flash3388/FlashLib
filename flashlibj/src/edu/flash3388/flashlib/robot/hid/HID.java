package edu.flash3388.flashlib.robot.hid;

/**
 * Interface for Human Interface Devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface HID {
	
	/**
	 * Gets the HID channel number.
	 * @return channel
	 */
	int getChannel();

	/**
	 * Gets the value of an axis from the human interface device.
	 * 
	 * @param axis the axis index
	 * @return the value of the axis [-1...1]
	 */
	double getRawAxis(int axis);
	
	/**
	 * Gets the value of an button from the human interface device.
	 * 
	 * @param button the button index
	 * @return the value of the button [false...true]
	 */
	boolean getRawButton(int button);

	/**
	 * Gets the button object for the button at the given index
	 * @param button the button index
	 * @return the button wrapper
	 */
	Button getButton(int button);

	/**
	 * Gets the amount of buttons on the interface device.
	 * @return the count of buttons
	 */
	int getButtonCount();
	
	/**
	 * Gets the stick object at the given index
	 * @param index the index of the stick
	 * @return stick wrapper
	 */
	Stick getStick(int index);
	
	/**
	 * Gets the value of a POV from the device
	 * 
	 * @param pov the pov index
	 * @return the value of the pov in degrees
	 */
	int getRawPov(int pov);

	/**
	 * Gets the POV object wrapper for the POV on this device
	 * @return the POV wrapper
	 */
	POV getPOV();
}
