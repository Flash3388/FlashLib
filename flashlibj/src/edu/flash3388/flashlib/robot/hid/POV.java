package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.beans.IntegerSource;

public class POV implements IntegerSource{

	private int stick;
	private int num;
	
	/**
	 * Creates a POV wrapper object
	 * 
	 * @param stick The Joystick the D-Pad is on.
	 * @param num the number of the D-Pad on the controller.
	 */
	public POV(int stick, int num){
		this.stick = stick;
		this.num = num;
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
	public final int getPOVNumber(){
		return num;
	}
	
	@Override
	public int get(){
		return RobotFactory.getImplementation().getHIDInterface().getHIDPOV(stick, num);
	}
}
