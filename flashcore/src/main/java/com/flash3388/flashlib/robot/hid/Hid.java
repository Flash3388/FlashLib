package com.flash3388.flashlib.robot.hid;

/**
 * Interface for Human Interface Devices.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Hid {
	
	/**
	 * Gets the Hid channel number.
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
     * Axes on the interface device.
     *
     * @return {@link Iterable} object for iterating over the axes.
     */
	Iterable<Axis> axes();

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
     * Buttons on the interface device.
     *
     * @return {@link Iterable} object for iterating over the buttons.
     */
    Iterable<Button> buttons();

	/**
	 * Gets the Pov object at the given index.
     *
     * @param pov pov index, from 0.
     *
	 * @return Pov
	 */
	Pov getPov(int pov);

    /**
     * Gets the amount of POVs on the interface device.
     *
     * @return the amount of POVs.
     */
	int getPovCount();

    /**
     * sPOVs on the interface device.
     *
     * @return {@link Iterable} object for iterating over the povs.
     */
    Iterable<Pov> povs();
}
