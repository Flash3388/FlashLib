package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.util.beans.DoubleSource;

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
public interface Encoder extends IOPort, DoubleSource, PIDSource{
	
	/**
	 * Enumeration of encoder data types. Using this, it is possible to indicate
	 * which values will be returned when {@link #pidGet()} and {@link #get()} are called.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.2.0
	 */
	public static enum EncoderDataType{
		Distance, Rate, Velocity
	}
	
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
	
	/**
	 * Gets the {@link EncoderDataType} value for this sensor.
	 * <p>
	 * This value is used by {@link #pidGet()} and {@link #get()} to determine which
	 * gyroscope data will be returned when they are called.
	 * 
	 * @return the current data type
	 */
	EncoderDataType getDataType();
	/**
	 * Sets the {@link EncoderDataType} value for this sensor.
	 * <p>
	 * This value is used by {@link #pidGet()} and {@link #get()} to determine which
	 * gyroscope data will be returned when they are called.
	 * 
	 * @param type the current data type
	 */
	void setDataType(EncoderDataType type);
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns the value of {@link #pidGet()}.
	 */
	@Override
	default double get() {
		return pidGet();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Depending on the value of {@link #getDataType()}, the following values will be returned:
	 * <ul>
	 * 	<li> {@link EncoderDataType#Distance}: {@link #getDistance()} </li>
	 * 	<li> {@link EncoderDataType#Rate}: {@link #getRate()} </li>
	 *  <li> {@link EncoderDataType#Velocity}: {@link #getVelocity()} </li>
	 * </ul>
	 */
	@Override
	default double pidGet() {
		switch (getDataType()) {
			case Distance: return getDistance();
			case Rate: return getRate();
			case Velocity: return getVelocity();
		}
		return 0.0;
	}
}
