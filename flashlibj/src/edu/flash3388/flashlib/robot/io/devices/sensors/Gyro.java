package edu.flash3388.flashlib.robot.io.devices.sensors;

import edu.flash3388.flashlib.robot.control.PIDSource;
import edu.flash3388.flashlib.robot.io.IOPort;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Interface for gyroscope sensors. Gyroscope sensors measure angular rotation and are used to measure
 * the angular position in one or more axes of an object they are placed on.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Gyro extends IOPort, PIDSource, DoubleSource{
	
	/**
	 * Enumeration of gyroscope data types. Using this, it is possible to indicate
	 * which values will be returned when {@link #pidGet()} and {@link #get()} are called.
	 * 
	 * @author Tom Tzook
	 * @since FlashLib 1.2.0
	 */
	public static enum GyroDataType{
		Angle, Rate
	}
	
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
	
	/**
	 * Gets the {@link GyroDataType} value for this gyroscope.
	 * <p>
	 * This value is used by {@link #pidGet()} and {@link #get()} to determine which
	 * gyroscope data will be returned when they are called.
	 * 
	 * @return the current data type
	 */
	GyroDataType getDataType();
	/**
	 * Sets the {@link GyroDataType} value for this gyroscope.
	 * <p>
	 * This value is used by {@link #pidGet()} and {@link #get()} to determine which
	 * gyroscope data will be returned when they are called.
	 * 
	 * @param type the current data type
	 */
	void setDataType(GyroDataType type);
	
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
	 * 	<li> {@link GyroDataType#Angle}: {@link #getAngle()} </li>
	 * 	<li> {@link GyroDataType#Rate}: {@link #getRate()} </li>
	 * </ul>
	 */
	@Override
	default double pidGet() {
		switch (getDataType()) {
			case Angle: return getAngle();
			case Rate: return getRate();
		}
		return 0.0;
	}
}
