package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.hid.HIDInterface;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;

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
	 * Gets the initialized {@link RobotModeSupplier} object for the robot.
	 * <p>
	 * This object will be used by base methods for operation mode data.
	 * 
	 * @return robot mode selector, or null if not initialized.
	 */
	RobotModeSupplier getModeSelector();

	/**
	 * Gets the current operation mode set by the {@link RobotModeSupplier} object of the robot.
	 * <p>
	 * The default implementation gets the mode selector by calling {@link #getModeSelector()}. If the
	 * returned value is null, {@link RobotModeSupplier#MODE_DISABLED} is returned, otherwise {@link RobotModeSupplier#getMode()}
	 * is returned.
	 * 
	 * @return current mode set by the robot's mode selector, or disabled if not mode selector was set.
	 */
	default int getMode(){
		return getModeSelector() == null? RobotModeSupplier.MODE_DISABLED : getModeSelector().getMode();
	}

	/**
	 * Gets whether or not the current mode set by the robot's {@link RobotModeSupplier} object is equal
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
	 * The default implementation calls {@link #isMode(int)} and passes it {@link RobotModeSupplier#MODE_DISABLED}.
	 * 
	 * @return true if in disabled mode, false otherwise
	 */
	default boolean isDisabled(){
		return isMode(RobotModeSupplier.MODE_DISABLED);
	}

	HIDInterface getHidInterface();
}
