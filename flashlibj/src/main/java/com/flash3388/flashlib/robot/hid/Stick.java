package com.flash3388.flashlib.robot.hid;

import com.jmath.vectors.Vector2;

/**
 * Represents a simple 2-axis stick from a controller: X-Axis, Y-Axis.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Stick {

	private final Axis mAxisX;
	private final Axis mAxisY;

	public Stick(Axis axisX, Axis axisY){
		mAxisX = axisX;
		mAxisY = axisY;
	}
	
	/**
	 * Gets the value of the x-axis of the controller
	 * @return the x-axis
	 */
	public double getX() {
		return mAxisX.get();
	}

	/**
	 * Gets the value of the y-axis of the controller
	 * @return the y-axis
	 */
	public double getY() {
		return mAxisY.get();
	}
	
	/**
	 * Gets the polar magnitude for the stick. 
	 * @return magnitude
	 */
	public double getMagnitude(){
		return new Vector2(getX(), getY()).magnitude();
	}

	/**
	 * Gets the polar angle for the stick from the y-axis. 
	 * @return angle in degrees
	 */
	public double getAngle(){
        return new Vector2(getX(), getY()).angle();
	}
}
