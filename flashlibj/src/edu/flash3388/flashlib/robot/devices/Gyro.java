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
	/**
	 * Gets the angle of the robot measured by the sensor.
	 * @return angle of the robot with degrees
	 */
	double getAngle();
	
	@Override
	default double get(){
		return getAngle();
	}
	@Override
	default double pidGet(){
		return getAngle();
	}
}
