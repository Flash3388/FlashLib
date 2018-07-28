package edu.flash3388.flashlib.robot.control;

import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * PID source is an interface for feedback data to the PID control loop. It is used to determine the current error
 * to be fixed by the loop.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 * @see PIDController
 */
public interface PIDSource extends DoubleSource {
	

	/**
	 * Gets the feedback data of the sensor. Provides error data to the control loop.
	 * @return the feedback data from the sensor.
	 */
	double pidGet();
}
