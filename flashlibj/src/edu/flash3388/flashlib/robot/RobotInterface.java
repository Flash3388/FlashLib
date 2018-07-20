package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.modes.ModeSelector;

/**
 * An interface for the current robot implementation. This interface is used by
 * the robot framework to access data about robot operations. When initializing robot operations,
 * an implementation of this interface is set to {@link RobotFactory} and can be accessed by
 * {@link RobotFactory#getImplementation()}.
 * <p>
 * For non-FRC robots, {@link RobotBase} implements most of this interface.
 * <p>
 * For FRC robot, {@link edu.flash3388.flashlib.robot.frc.FRCRobotBase FRCRobotBase} implements this interface.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public interface RobotInterface {
	
	/**
	 * Gets the initialized {@link ModeSelector} object for the robot.
	 * <p>
	 * This object will be used by base methods for operation mode data.
	 * 
	 * @return robot mode selector, or null if not initialized.
	 */
	ModeSelector getModeSelector();
	/**
	 * Gets the current operation mode set by the {@link ModeSelector} object of the robot.
	 * <p>
	 * The default implementation gets the mode selector by calling {@link #getModeSelector()}. If the
	 * returned value is null, {@link ModeSelector#MODE_DISABLED} is returned, otherwise {@link ModeSelector#getMode()}
	 * is returned.
	 * 
	 * @return current mode set by the robot's mode selector, or disabled if not mode selector was set.
	 */
	default int getMode(){
		return getModeSelector() == null? ModeSelector.MODE_DISABLED : getModeSelector().getMode();
	}
	/**
	 * Gets whether or not the current mode set by the robot's {@link ModeSelector} object is equal
	 * to a given mode value. If true, this indicates that the current mode is the given mode.
	 * <p>
	 * The default implementation calls {@link #getMode()} and gets whether the returned value
	 * is equal to the given value.
	 * 
	 * @param mode the mode to check
	 * @return true if the given mode is the current operation mode, false otherwise
	 * @see #getMode()
	 */
	default boolean isMode(int mode){
		return getMode() == mode;
	}
	
	/**
	 * Gets whether or not the robot is currently in disabled mode. Disabled mode
	 * is a safety mode where the robot does nothing.
	 * <p>
	 * The default implementation calls {@link #isMode(int)} and passes it {@link ModeSelector#MODE_DISABLED}.
	 * 
	 * @return true if in disabled mode, false otherwise
	 */
	default boolean isDisabled(){
		return isMode(ModeSelector.MODE_DISABLED);
	}
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
