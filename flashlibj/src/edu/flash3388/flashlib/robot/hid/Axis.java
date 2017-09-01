package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.util.beans.DoubleSource;

public class Axis implements DoubleSource{

	private HID hid;
	private int axis;
	
	/**
	 * Creates a new trigger.
	 * @param hid the device
	 * @param number the axis index
	 */
	public Axis(HID hid, int number) {
		this.hid = hid;
		this.axis = number;
	}
	
	/**
	 * Get the HID
	 * @return hid
	 */
	public final HID getHID(){
		return hid;
	}
	/**
	 * Get the axis number
	 * @return axis number
	 */
	public final int getAxisNumber(){
		return axis;
	}
	
	/**
	 * Gets the value of the trigger.
	 * @return the value of the trigger
	 */
	public double get(){
		return hid.getRawAxis(axis);
	}
}
