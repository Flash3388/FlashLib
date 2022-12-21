package com.flash3388.flashlib.app;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides access to control and status components of the application. An application can only have one instance of this type.
 * Using this instance, the application code can then utilize the different methods from this interface in order to
 * control different parts of the application.
 * <p>
 *     The actual implementation used should suit the specific needs of the app. This can be affected by many things,
 *     such as platform, control architecture and more. Thus, it is best to be aware of the implementation.
 * </p>
 *
 * @since FlashLib 3.2.0
 *
 * @see BasicFlashLibControl
 */
public interface FlashLibControl {

    /**
     * Gets the {@link InstanceId} of the currently running application. The instance id
     * is a process-specific identifier which represents an instance of <em>FlashLib</em>
     * application.
     *
     * @return {@link InstanceId}
     */
    InstanceId getInstanceId();

    /**
     * Gets the {@link Clock} object for the application. Capable of providing timestamp.
     * This clock should generally be the only one used (or the base for other clocks)
     * in the robot.
     * <pre>
     *     Time now = control.getClock().currentTime();
     * </pre>
     *
     * @return {@link Clock} of the application.
     */
    Clock getClock();

    /**
     * Gets the {@link Logger} object for the application. Used for logging run data
     * and errors for debugging.
     * <pre>
     *     control.getLogger().error("Everything is broken, help me");
     * </pre>
     * The destination of the log data is dependent on the implementation.
     *
     * @return {@link Logger} of the application.
     */
    Logger getLogger();

    /**
     * Registers new resources in use by the application in the form of {@link AutoCloseable}.
     * These resources will be closed automatically when the app stops running.
     *
     * @param closeables resources to close on app shutdown.
     *
     * @see FlashLibApp#shutdown(FlashLibControl)
     */
    void registerCloseables(Collection<? extends AutoCloseable> closeables);

    /**
     * Registers new resources in use by the application in the form of {@link AutoCloseable}.
     * These resources will be closed automatically when the app stops running.
     *
     * @param closeables resources to close on app shutdown.
     *
     * @see FlashLibApp#shutdown(FlashLibControl)
     */
    default void registerCloseables(AutoCloseable... closeables) {
        registerCloseables(Arrays.asList(closeables));
    }

    /**
     * Gets the registry which holds and controls {@link com.castle.concurrent.service.Service services}.
     * <p>
     *     Services registered here will be automatically started on application initialization and automatically
     *     stopped on application shutdown.
     * </p>
     *
     * @return {@link ServiceRegistry}
     */
    ServiceRegistry getServiceRegistry();

}
