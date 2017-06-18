package edu.flash3388.flashlib.robot.devices;

/**
 * Interface for gyroscope sensors. Gyroscope are used for measuring angular rotation of the robot.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface Gyro {
	/**
	 * Gets the angle of the robot measured by the sensor.
	 * @return angle of the robot with degrees
	 */
	double getAngle();
}
