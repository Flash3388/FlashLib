package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Interface for encoder sensors. Encoders are used for measuring angular rotation of axes or wheels.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Encoder extends IOPort, DoubleSource, PIDSource{
	
	public static enum PIDType{
		Distance, Rate, Velocity
	}
	
	/**
	 * Resets the encoder. 
	 */
	void reset();
	
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
	
	PIDType getPIDType();
	void setPIDType(PIDType type);
	
	@Override
	default double get() {
		return pidGet();
	}
	@Override
	default double pidGet() {
		switch (getPIDType()) {
			case Distance: return getDistance();
			case Rate: return getRate();
			case Velocity: return getVelocity();
		}
		return 0.0;
	}
}
