package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotFactory;

/**
 * Represents a simple 2-axis stick from a controller: X-Axis, Y-Axis.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Stick{

	private int stick, axisX, axisY;
	
	/**
	 * Creates a new stick for a controller.
	 * 
	 * @param stick the stick index
	 * @param axisX the index of the x-axis
	 * @param axisY the index of the y-axis
	 */
	public Stick(int stick, int axisX, int axisY){
		this.axisX = axisX;
		this.stick = stick;
		this.axisY = axisY;
	}
	
	/**
	 * Gets the value of the x-axis of the controller
	 * @return the x-axis
	 */
	public double getX() {
		return RobotFactory.getStickAxis(stick, axisX);
	}
	/**
	 * Gets the value of the y-axis of the controller
	 * @return the y-axis
	 */
	public double getY() {
		return RobotFactory.getStickAxis(stick, axisY);
	}
	
	/**
	 * Gets the polar magnitude for the stick. 
	 * @return magnitude
	 */
	public double getMagnitude(){
		double x = getX(), y = getY();
		return Math.sqrt(x * x + y * y);
	}
	/**
	 * Gets the polar angle for the stick from the y-axis. 
	 * @return angle in degrees
	 */
	public double getAngle(){
		return Math.toDegrees(Math.atan2(-getX(), -getY()));
	}
}
