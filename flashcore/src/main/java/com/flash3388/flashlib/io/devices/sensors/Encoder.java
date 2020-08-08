package com.flash3388.flashlib.io.devices.sensors;

import java.io.Closeable;

/**
 * Interface for relative encoder sensors. Relative encoders measure the rotation of wheels axes and are used to get the 
 * rotation rate of axes, distance passed by wheels or even linear velocity.
 * <p>
 * In reality, relative encoder simply measure parts of rotations and send a pulse through a digital channel, 
 * but use those it is possible to calculate a lot of data. For example, to calculate rotation rate, the time between
 * 2 pulses is calculated and then the amount of degrees passed during those 2 pulses is divided by the time.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Encoder extends Closeable {
	
	/**
	 * Resets the encoder. 
	 */
	void reset();
	
	/**
	 * Gets the raw count of pulses measured by the encoder.
	 * 
	 * @return pulse count.
	 */
	int getRaw();

	/**
	 * Gets the rate of rotation measured by the encoder.
	 * 
	 * @return rate of rotation in RPM.
	 */
	double getRate();

	/**
	 * Gets the current linear velocity measured by the encoder.
	 * <p>
	 * Linear velocity is measured by getting the linear distance passed in a time period.
	 * </p>
	 * 
	 * @return linear velocity in meters per second.
	 */
	double getVelocity();

	/**
	 * Gets the distance passed by the wheel the sensor measures. 
	 * <p>
	 * Distance is measured by the amount of completed revolutions measured by the sensor. This value 
	 * is than multiplied by a constant which describes the distance passed per one revolution. For wheels,
	 * that value is equal to the circumference of the wheel.
	 * </p>
	 * 
	 * @return distance passed in meters.
	 */
	double getDistance();

	/**
	 * Gets the rotation direction measured by the encoder. If the encoder is not quadrature, rotation direction
	 * cannot be determined and the value returned will always be true.
	 * 
	 * @return true for clockwise rotation, false for counter-clockwise rotation.
	 */
	boolean getDirection();
}
