package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.hid.HidInterface;
import edu.flash3388.flashlib.robot.modes.RobotMode;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;

/**
 * <p>
 *     The base class for robot main classes.
 * </p>
 * Inheriting robot classes need to implement the following methods:
 * <ul>
 *     <li>{@link #robotInit()}: called when the robot is initialized.
 *          Should be used to initialize robot components</li>
 *     <li>{@link #robotMain()}: the robot main method. Called after initialization,
 *           and should implement the robot logic.</li>
 *     <li>{@link #robotShutdown()}: called after {@link #robotMain()} is finished. Used
 *           for freeing resources and components initialized in {@link #robotInit()}.</li>
 * </ul>
 * <p>
 *     If {@link #robotMain()} throws an exception, {@link #robotShutdown()} is called.
 * </p>
 *
 * @since FlashLib 1.3.0
 */
public abstract class Robot {

    /**
     * Gets the initialized {@link RobotModeSupplier} object for the robot.
     * <p>
     * This object will be used by base methods for operation mode data.
     *
     * @return robot mode selector, or null if not initialized.
     */
    public abstract RobotModeSupplier getModeSupplier();

    /**
     * Gets the current operation mode set by the {@link RobotModeSupplier} object of the robot.
     * <p>
     * The default implementation gets the mode selector by calling {@link #getModeSupplier()}. If the
     * returned value is null, {@link RobotMode#DISABLED} is returned, otherwise {@link RobotModeSupplier#getMode()}
     * is returned.
     *
     * @return current mode set by the robot's mode selector, or disabled if not mode selector was set.
     */
    public RobotMode getMode(){
        return getModeSupplier() == null ? RobotMode.DISABLED : getModeSupplier().getMode();
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
    public boolean isInMode(RobotMode mode){
        return getMode().equals(mode);
    }

    /**
     * Gets whether or not the robot is currently in disabled mode. Disabled mode
     * is a safety mode where the robot does nothing.
     *
     * @return true if in disabled mode, false otherwise
     */
    public boolean isDisabled(){
        return isInMode(RobotMode.DISABLED);
    }

    public abstract HidInterface getHidInterface();

	//--------------------------------------------------------------------
	//------------------------Robot Flow----------------------------------
	//--------------------------------------------------------------------

	/**
	 * Called when robot initialization starts, allowing for initialization of user code.
	 *
	 * @throws RobotInitializationException if an error occurs while initializing
	 */
	protected abstract void robotInit() throws RobotInitializationException;

	/**
	 * Called when {@link Robot} finished initialization and the robot can be started.
	 * This is the main method of the robot and all operations should be directed from here.
	 */
    protected abstract void robotMain();

	/**
	 * Called when the robot finishes running, allowing to perform custom stop operations. Should be used
	 * to free robot components.
	 */
    protected abstract void robotShutdown();
}
