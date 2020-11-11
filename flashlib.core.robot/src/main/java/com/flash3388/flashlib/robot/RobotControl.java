package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.base.RobotBase;
import com.flash3388.flashlib.robot.base.generic.GenericRobotControl;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SchedulerMode;
import com.flash3388.flashlib.scheduling.actions.Action;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Provides access to control and status components of the robot. A robot can only have one instance of this type.
 * Using this instance, the robot code can then utilize the different methods from this interface in order to
 * control different parts of the robot.
 * <p>
 *     The actual implementation used should suit the specific needs of the robot. This can be affected by many things,
 *     such as platform, driver control architecture and more. Thus, it is best to be aware of the implementation.
 * </p>
 *
 * @since FlashLib 3.0.0
 *
 * @see GenericRobotControl
 */
public interface RobotControl {

    /**
     * Gets the initialized {@link Supplier} object for {@link RobotMode} of the robot.
     * <p>
     * This object will be used by base methods for operation mode data.
     *
     * @return robot mode selector, or null if not initialized.
     */
    Supplier<? extends RobotMode> getModeSupplier();

    /**
     * Gets the current operation mode set by the {@link #getModeSupplier()} object of the robot.
     * <p>
     * The default implementation gets the mode selector by calling {@link #getModeSupplier()}. If the
     * returned value is null, {@link RobotMode#DISABLED} is returned, otherwise {@link Supplier#get()}
     * is returned.
     *
     * @return current mode set by the robot's mode selector, or disabled if not mode selector was set.
     */
    default RobotMode getMode() {
        return getModeSupplier() == null ? RobotMode.DISABLED : getModeSupplier().get();
    }

    /**
     * Gets the current operation mode set by the {@link #getModeSupplier()} object of the robot.
     * <p>
     * The returned mode is cast to a desired type of {@link RobotMode}.
     * <p>
     * The default implementation gets the mode selector by calling {@link #getModeSupplier()}. If the
     * returned value is null, {@link RobotMode#DISABLED} is returned, otherwise {@link Supplier#get()}
     * is returned.
     *
     * @param type class object representing the type of the {@link RobotMode} desired.
     *             Should be a class implementing it.
     * @param <T> type parameter of the {@link RobotMode} desired. Should be a class implementing it.
     *
     * @return current mode set by the robot's mode selector, or disabled if not mode selector was set.
     *
     * @see RobotMode#cast(RobotMode, Class)
     */
    default <T extends RobotMode> T getMode(Class<T> type) {
        Supplier<? extends RobotMode> supplier = getModeSupplier();
        if (supplier == null) {
            throw new IllegalStateException("No supplier set");
        }

        return RobotMode.cast(supplier.get(), type);
    }

    /**
     * Gets whether or not the current mode set by the robot's {@link #getModeSupplier()} object is equal
     * to a given mode value. If true, this indicates that the current mode is the given mode.
     * <p>
     * The default implementation calls {@link #getMode()} and gets whether the returned value
     * is equal to the given value.
     *
     * @param mode the mode to check
     * @return true if the given mode is the current operation mode, false otherwise
     * @see #getMode()
     */
    default boolean isInMode(RobotMode mode) {
        return getMode().equals(mode);
    }

    /**
     * Gets whether or not the robot is currently in disabled mode. Disabled mode
     * is a safety mode where the robot does nothing.
     *
     * @return true if in disabled mode, false otherwise
     */
    default boolean isDisabled() {
        return getMode().isDisabled();
    }

    /**
     * Gets the {@link IoInterface} object associated with the robot. Can
     * be used to create hardware input/output connections for sensors, actuators
     * and other electronic devices.
     * <pre>
     *     IoChannel channel = Robot.newIoChannel(...);
     *     Pwm pwm = robotControl.getIoInterface().newPwm(channel);
     *     Talon talon = new Talon(pwm);
     * </pre>
     * Note that <code>Robot</code> is not an actual class, and just an example.
     * See {@link com.flash3388.flashlib.io.IoChannel} for information about how
     * to create channels.
     *
     * @return {@link IoInterface} of the robot.
     */
    IoInterface getIoInterface();

    /**
     * Gets the {@link HidInterface} object associated with the robot. Can
     * be used to create human interface devices connections for receiving
     * input from the user.
     * <pre>
     *     HidChannel channel = Robot.newHidChannel(...);
     *     Joystick joystick = robotControl.getHidInterface().newJoystick(channel);
     * </pre>
     * Note that <code>Robot</code> is not an actual class, and just an example.
     * See {@link com.flash3388.flashlib.hid.HidChannel} for information about how
     * to create channels.
     *
     * @return {@link HidInterface} of the robot.
     */
    HidInterface getHidInterface();

    /**
     * Gets the {@link Scheduler} object of the robot. Responsible for
     * executing {@link com.flash3388.flashlib.scheduling.actions.Action Actions}.
     * <p>
     *     This normally has not much use directly, since starting actions
     *     can be done with {@link Action#start()}. However, the scheduler provides
     *     some additional functionality which some may find useful. For example,
     *     canceling all actions:
     * </p>
     * <pre>
     *     robotControl.getScheduler().cancelAllActions();
     * </pre>
     * See {@link Scheduler} for further functionality.
     * <p>
     *     Note that {@link Scheduler#run(SchedulerMode)} should only be used
     *     by robot bases and never called directly by the robot code. This method
     *     executes the scheduler iteration, and should be managed internally only.
     * </p>
     *
     * @return {@link Scheduler} of the robot.
     */
    Scheduler getScheduler();

    /**
     * Gets the {@link Clock} object for the robot. Capable of providing timestamp.
     * This clock should generally be the only one used (or the base for other clocks)
     * in the robot.
     * <pre>
     *     Time now = robotControl.getClock().currentTime();
     * </pre>
     *
     * @return {@link Clock} of the robot.
     */
    Clock getClock();

    /**
     * Gets the {@link Logger} object for the robot. Used for logging run data
     * and errors for debugging.
     * <pre>
     *     robotControl.getLogger().error("Everything is broken, help me");
     * </pre>
     * The destination of the log data is dependent on the creation of the
     * robot base.
     *
     * @return {@link Logger} of the robot.
     */
    Logger getLogger();

    /**
     * Registers new resources in use by the robot in the form of {@link AutoCloseable}.
     * These resources will be closed automatically when the robot stops running.
     *
     * @param closeables collection of resources to close on robot stop.
     *
     * @see RobotBase#robotShutdown()
     */
    void registerCloseables(Collection<? extends AutoCloseable> closeables);

    /**
     * Registers new resources in use by the robot in the form of {@link AutoCloseable}.
     * These resources will be closed automatically when the robot stops running.
     *
     * @param closeables resources to close on robot stop.
     *
     * @see RobotBase#robotShutdown()
     */
    default void registerCloseables(AutoCloseable... closeables) {
        registerCloseables(Arrays.asList(closeables));
    }
}
