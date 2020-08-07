package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.resources.CloseableResource;
import com.flash3388.flashlib.util.resources.Resource;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Collectors;


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
    default RobotMode getMode(){
        return getModeSupplier() == null ? RobotMode.DISABLED : getModeSupplier().get();
    }

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
    default boolean isInMode(RobotMode mode){
        return getMode().equals(mode);
    }

    /**
     * Gets whether or not the robot is currently in disabled mode. Disabled mode
     * is a safety mode where the robot does nothing.
     *
     * @return true if in disabled mode, false otherwise
     */
    default boolean isDisabled(){
        return getMode().isDisabled();
    }

    IoInterface getIoInterface();

    HidInterface getHidInterface();

    Scheduler getScheduler();

    Clock getClock();

    Logger getLogger();

    void registerResources(Collection<? extends Resource> resources);

    default void registerResources(Resource... resources) {
        registerResources(Arrays.asList(resources));
    }

    default void registerCloseables(Collection<? extends AutoCloseable> closeables) {
        Logger logger = getLogger();
        registerResources(closeables.stream()
                .map((c)->new CloseableResource(c, logger))
                .collect(Collectors.toList()));
    }

    default void registerCloseables(AutoCloseable... closeables) {
        registerCloseables(Arrays.asList(closeables));
    }
}
