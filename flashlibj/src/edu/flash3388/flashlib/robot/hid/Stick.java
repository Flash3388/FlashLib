package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.devices.DoubleDataSource;

/**
 * Represents a simple 2-axis stick from a joystick: X-Axis, Y-Axis.
 * 
 * @author Tom Tzook
 */
public abstract class Stick implements DoubleDataSource{

	private boolean xreturn = false;	
	
	/**
	 * Gets the value of the X axis of the stick
	 * @return Value of X axis
	 */
	public abstract double getX();
	/**
	 * Gets the value of the Y axis of the stick
	 * @return Value of Y axis
	 */
	public abstract double getY();
	
	public double getMagnitude(){
		double x = getX(), y = getY();
		return Math.sqrt(x * x + y * y);
	}
	public double getAngle(){
		return Math.toDegrees(Math.atan2(-getX(), -getY()));
	}

	public void setSourceToX(){
		xreturn = true;
	}
	public void setSourceToY(){
		xreturn = false;
	}
	public boolean isSourceX(){
		return xreturn;
	}
	public boolean isSourceY(){
		return !xreturn;
	}
	@Override
	public double get() {
		return xreturn? getX() : getY();
	}
}
