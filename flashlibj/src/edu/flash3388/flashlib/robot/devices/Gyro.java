package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * Interface for gyroscope sensors. Gyroscope are used for measuring angular rotation of the robot.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface Gyro extends IOPort, PIDSource, DoubleSource{
	
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
	
	
	
	GyroDataType getDataType();
	void setDataType(GyroDataType type);
	
	@Override
	default double get() {
		return pidGet();
	}
	@Override
	default double pidGet() {
		switch (getDataType()) {
			case Angle: return getAngle();
			case Rate: return getRate();
		}
		return 0.0;
	}
}
