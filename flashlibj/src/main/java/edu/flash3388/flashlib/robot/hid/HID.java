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
     *
	 * @return channel
	 */
	int getChannel();

    /**
     * Gets the axis object at the given index.
     *
     * @param axis axis index, from 0.
     *
     * @return axis
     */
	Axis getAxis(int axis);

    /**
     * Gets the amount of axes on the interface device.
     *
     * @return the amount of axes.
     */
	int getAxisCount();

	/**
	 * Gets the button object for the button at the given index.
     *
	 * @param button button index, from 0.
     *
	 * @return button
	 */
	Button getButton(int button);

	/**
	 * Gets the amount of buttons on the interface device.
     *
	 * @return the amount of buttons
	 */
	int getButtonCount();

	/**
	 * Gets the POV object at the given index.
     *
     * @param pov pov index, from 0.
     *
	 * @return POV
	 */
	POV getPOV(int pov);

    /**
     * Gets the amount of POVs on the interface device.
     *
     * @return the amount of POVs.
     */
	int getPovCount();
}
