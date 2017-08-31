package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class Trigger implements DoubleSource{

	private int stick, axis;
	
	/**
	 * Creates a new trigger.
	 * @param stick the device index
	 * @param number the axis index
	 */
	public Trigger(int stick, int number) {
		this.stick = stick;
		this.axis = number;
	}
	
	/**
	 * Get the HID channel
	 * @return hid channel
	 */
	public final int getChannel(){
		return stick;
	}
	/**
	 * Get the POV number
	 * @return POV number
	 */
	public final int getAxisNumber(){
		return axis;
	}
	
	/**
	 * Gets the value of the trigger.
	 * @return the value of the trigger
	 */
	public double get(){
		return RobotFactory.getImplementation().getHIDInterface().getHIDAxis(stick, axis);
	}
}
