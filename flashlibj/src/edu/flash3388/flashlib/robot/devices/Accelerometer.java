package edu.flash3388.flashlib.robot.devices;
/**
 * Interface for accelerometer sensors. Accelerometers are used for measuring acceleration of the robot.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Accelerometer {
	/**
	 * Gets the x-axis acceleration
	 * @return acceleration along the x-axis
	 */
	double getX();
	/**
	 * Gets the y-axis acceleration
	 * @return acceleration along the y-axis
	 */
	double getY();
	/**
	 * Gets the z-axis acceleration
	 * @return acceleration along the z-axis
	 */
	double getZ();
}
