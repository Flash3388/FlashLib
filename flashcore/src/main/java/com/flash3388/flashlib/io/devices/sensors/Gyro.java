package com.flash3388.flashlib.io.devices.sensors;

import java.io.Closeable;

/**
 * Interface for gyroscope sensors. Gyroscope sensors measure angular rotation and are used to measure
 * the angular position in one or more axes of an object they are placed on.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Gyro extends Closeable {
	
	/**
	 * Resets the sensor values.
	 */
	void reset();
	
	/**
	 * Gets the angle of the robot measured by the sensor.
	 * 
	 * @return angle of the robot with degrees
	 */
	double getAngle();

	/**
	 * Gets the rate of angular rotation measured by the sensor.
	 * 
	 * @return rate of rotation in degrees per second
	 */
	double getRate();
}
