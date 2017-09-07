package edu.flash3388.flashlib.robot.hid;

/**
 * Represents a simple 2-axis stick from a controller: X-Axis, Y-Axis.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class Stick{

	private Axis axisX, axisY;
	
	/**
	 * Creates a new stick for a controller.
	 * 
	 * @param hid the HID
	 * @param axisX the index of the x-axis
	 * @param axisY the index of the y-axis
	 */
	public Stick(HID hid, int axisX, int axisY){
		this.axisX = new Axis(hid, axisX);
		this.axisY = new Axis(hid, axisY);
	}
	
	/**
	 * Gets the x axis {@link Axis} object.
	 * @return x axis
	 */
	public Axis getXAxis(){
		return axisX;
	}
	/**
	 * Gets the y axis {@link Axis} object.
	 * @return y axis
	 */
	public Axis getYAxis(){
		return axisY;
	}
	
	/**
	 * Gets the value of the x-axis of the controller
	 * @return the x-axis
	 */
	public double getX() {
		return axisX.get();
	}
	/**
	 * Gets the value of the y-axis of the controller
	 * @return the y-axis
	 */
	public double getY() {
		return axisY.get();
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
