package com.flash3388.flashlib.io.devices;

import java.io.Closeable;

/**
 * Interface for accelerometer sensors with 3 axes.
 * Accelerometers are used for measuring acceleration of the robot.
 *
 * @since FlashLib 3.0.0
 */
public interface Accelerometer3 extends Closeable {

	/**
	 * Gets the x-axis acceleration
	 * @return acceleration along the x-axis in G
	 */
	double getX();

	/**
	 * Gets the y-axis acceleration
	 * @return acceleration along the y-axis in G
	 */
	double getY();

	/**
	 * Gets the z-axis acceleration
	 * @return acceleration along the z-axis in G
	 */
	double getZ();
}
