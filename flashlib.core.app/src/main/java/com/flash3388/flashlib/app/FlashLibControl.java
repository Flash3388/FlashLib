package com.flash3388.flashlib.app;

import com.flash3388.flashlib.annotations.MainThreadOnly;
import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.app.net.NetworkingMode;
import com.flash3388.flashlib.app.watchdog.FeedReporter;
import com.flash3388.flashlib.app.watchdog.Watchdog;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

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
    @MainThreadOnly
    void registerCloseables(Collection<? extends AutoCloseable> closeables);

    /**
     * Registers new resources in use by the application in the form of {@link AutoCloseable}.
     * These resources will be closed automatically when the app stops running.
     *
     * @param closeables resources to close on app shutdown.
     *
     * @see FlashLibApp#shutdown(FlashLibControl)
     */
    @MainThreadOnly
    default void registerCloseables(AutoCloseable... closeables) {
        getMainThread().verifyCurrentThread();
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

    /**
     * Gets the {@link NetworkInterface} object associated with the application. Can be used
     * to communicate with other programs via several protocols, depending on
     * the configuration and the implementation of this interface.
     * Check {@link NetworkingMode} before accessing specific protocol to ensure
     * the protocol is indeed available.
     *
     * @return {@link NetworkInterface}
     */
    NetworkInterface getNetworkInterface();

    /**
     * Gets the {@link FlashLibMainThread} object representing the main thread
     * of the application.
     *
     * @return {@link FlashLibMainThread}
     */
    FlashLibMainThread getMainThread();

    /**
     * Creates a new watchdog, used to track run of services, threads or loops. As a way to ensure
     * it runs within a specific time.
     *
     * To use the watchdog, the target must periodically call {@link Watchdog#feed()}. As long as
     * it keeps updating the {@link Watchdog} in time, then we can assume that it is up and running.
     * If the target does not update the watchdog within a set amount of time, then it is considered <em>expired</em>
     * while will be reported.
     *
     * The watchdog must first be enabled for usage with {@link Watchdog#enable()}.
     *
     * @param name name of the watchdog
     * @param timeout time within the watchdog is expected to be updated, if the watchdog was not updated within
     *                 the given time, it is <em>expired</em> and this will be reported.
     * @param reporter reporter to updated on <em>expiration</em>
     * @return {@link Watchdog}
     *
     * @see #newWatchdog(String, Time)
     */
    Watchdog newWatchdog(String name, Time timeout, FeedReporter reporter);

    /**
     * Creates a new watchdog, used to track run of services, threads or loops. As a way to ensure
     * it runs within a specific time.
     *
     * To use the watchdog, the target must periodically call {@link Watchdog#feed()}. As long as
     * it keeps updating the {@link Watchdog} in time, then we can assume that it is up and running.
     * If the target does not update the watchdog within a set amount of time, then it is considered <em>expired</em>
     * while will be reported to the log.
     *
     * The watchdog must first be enabled for usage with {@link Watchdog#enable()}.
     *
     * @param name name of the watchdog
     * @param timeout time within the watchdog is expected to be updated, if the watchdog was not updated within
     *                 the given time, it is <em>expired</em> and this will be reported.
     * @return {@link Watchdog}
     *
     * @see #newWatchdog(String, Time, FeedReporter)
     */
    Watchdog newWatchdog(String name, Time timeout);

    /**
     * Creates a new thread to be used by the application. The created thread is configured
     * and monitored.
     *
     * @param name name of the thread
     * @param runnable task for the thread to execute
     * @return a new, un-started thread.
     */
    Thread newThread(String name, Consumer<? super FlashLibControl> runnable);
}
