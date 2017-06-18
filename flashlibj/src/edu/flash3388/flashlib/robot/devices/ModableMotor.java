package edu.flash3388.flashlib.robot.devices;

/**
 * Interface for speed controllers with the ability to switch between brake and coast modes through
 * the software.
 * <p>
 * Brake mode is a special controller mode which, when the value is set to 0, performs an emergency stop 
 * of the motor by shorting out the connectors. Coast mode is just the opposite, where when the value
 * is set 0, nothing occurs.
 * </p>
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public interface ModableMotor {
	/**
	 * Sets the motor controller to use brake mode or coast mode.
	 * 
	 * @param mode true for brake mode, false for coast mode
	 */
	void enableBrakeMode(boolean mode);
	/**
	 * Gets whether or not the motor controller is set to use brake mode or coast mode.
	 * @return true for brake mode, false for coast mode
	 */
	boolean inBrakeMode();
}
