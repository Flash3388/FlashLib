package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;

/**
 * Represents a simple 2-axis stick from a joystick: X-Axis, Y-Axis.
 * 
 * @author Tom Tzook
 */
public class Stick implements DoubleDataSource{

	private boolean xreturn = false;	
	private int stick, axisX, axisY;
	
	public Stick(int stick, int axisX, int axisY){
		this.axisX = axisX;
		this.stick = stick;
		this.axisY = axisY;
	}
	
	public double getX() {
		return RobotFactory.getStickAxis(stick, axisX);
	}
	public double getY() {
		return RobotFactory.getStickAxis(stick, axisY);
	}
	
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
