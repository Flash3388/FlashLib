package edu.flash3388.flashlib.robot;

/**
 * An interface for the current robot implementation. Provides for data about the robot.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public interface Robot {
	
	/**
	 * Gets whether or not the robot is currently in disabled mode. Disabled mode
	 * is a safety mode where the robot does nothing.
	 * 
	 * @return true if in disabled mode, false otherwise
	 */
	boolean isDisabled();
	/**
	 * Gets whether or not the robot is currently in operator control mode. Operator control
	 * mode is a mode where the robot is controlled by an operator and does not operator autonomously.
	 * 
	 * @return true if in operator control mode, false otherwise
	 */
	boolean isOperatorControl();
	/**
	 * Gets whether or not the current implementation is an FRC robot. Used to indicate if WPILib
	 * is currently used for electronics IO. If this is not the case, FlashLib will know to operate its own
	 * electronics IO features.
	 * 
	 * @return true if an FRC robot, false otherwise
	 */
	boolean isFRC();
}
