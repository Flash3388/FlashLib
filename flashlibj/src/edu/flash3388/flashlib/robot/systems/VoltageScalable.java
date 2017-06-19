package edu.flash3388.flashlib.robot.systems;

import edu.flash3388.flashlib.robot.FlashRoboUtil;

/**
 * Interface for systems using the scaling voltage feature from {@link FlashRoboUtil#scaleVoltageBus(double, double)}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface VoltageScalable {
	/**
	 * Sets whether or not to use voltage scaling when setting values to the motors.
	 * @param en true to enable, false to disable
	 * @see FlashRoboUtil#scaleVoltageBus(double, double)
	 */
	void enableVoltageScaling(boolean en);
	/**
	 * Gets whether or not to use voltage scaling when setting values to the motors.
	 * @return true if enabled, false otherwise
	 */
	boolean isVoltageScaling();
}
