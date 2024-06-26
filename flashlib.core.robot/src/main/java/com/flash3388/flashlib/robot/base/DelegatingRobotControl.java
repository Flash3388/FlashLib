package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.app.FlashLibControl;
import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.app.watchdog.FeedReporter;
import com.flash3388.flashlib.app.watchdog.Watchdog;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.io.devices.DeviceInterface;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.modes.RobotModeSupplier;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.concurrent.NamedThreadFactory;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * A helper class for robot bases to allow them having a more comfortable
 * syntax for using {@link RobotControl}.
 * By extending this, robot bases can now access {@link RobotControl} methods
 * by calling this class, so instead of:
 * <pre>
 *     class Robot {
 *         private final RobotControl mRobotControl;
 *
 *         public Robot(RobotControl robotControl) {
 *             mRobotControl = robotControl;
 *         }
 *
 *         void someMethod() {
 *             Time now = mRobotControl.getClock().currentTime();
 *             ....
 *         }
 *     }
 * </pre>
 * It is possible to:
 * <pre>
 *     class Robot extends DelegatingRobotControl {
 *         public Robot(RobotControl robotControl) {
 *             super(robotControl);
 *         }
 *
 *         void someMethod() {
 *             Time now = getClock().currentTime();
 *             ....
 *         }
 *     }
 * </pre>
 *
 * @since FlashLib 2.0.0
 */
public class DelegatingRobotControl implements RobotControl {

    private final RobotControl mRobotControl;

    protected DelegatingRobotControl(RobotControl robotControl) {
        mRobotControl = robotControl;
    }

    @Override
    public RobotModeSupplier getModeSupplier() {
        return mRobotControl.getModeSupplier();
    }

    @Override
    public IoInterface getIoInterface() {
        return mRobotControl.getIoInterface();
    }

    @Override
    public HidInterface getHidInterface() {
        return mRobotControl.getHidInterface();
    }

    @Override
    public Scheduler getScheduler() {
        return mRobotControl.getScheduler();
    }

    @Override
    public InstanceId getInstanceId() {
        return mRobotControl.getInstanceId();
    }

    @Override
    public Clock getClock() {
        return mRobotControl.getClock();
    }

    @Override
    public Logger getLogger() {
        return mRobotControl.getLogger();
    }

    @Override
    public void registerCloseables(Collection<? extends AutoCloseable> closeables) {
        mRobotControl.registerCloseables(closeables);
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return mRobotControl.getServiceRegistry();
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        return mRobotControl.getNetworkInterface();
    }

    @Override
    public DeviceInterface getDeviceInterface() {
        return mRobotControl.getDeviceInterface();
    }

    @Override
    public FlashLibMainThread getMainThread() {
        return mRobotControl.getMainThread();
    }

    @Override
    public Watchdog newWatchdog(String name, Time timeout, FeedReporter reporter) {
        return mRobotControl.newWatchdog(name, timeout, reporter);
    }

    @Override
    public Watchdog newWatchdog(String name, Time timeout) {
        return mRobotControl.newWatchdog(name, timeout);
    }

    @Override
    public NamedThreadFactory getThreadFactory() {
        return mRobotControl.getThreadFactory();
    }

    @Override
    public Thread newThread(String name, Consumer<? super FlashLibControl> runnable) {
        return mRobotControl.newThread(name, runnable);
    }
}
