package com.flash3388.flashlib.robot.base;

import com.castle.concurrent.service.Service;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.base.DependencyProvider;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.services.ServiceRegistry;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.function.Supplier;

/**
 * A simple implementation of {@link RobotControl} which receives all required components via the constructor.
 *
 * @since FlashLib 3.0.0
 */
public class GenericRobotControl implements RobotControl {

    private final Logger mLogger;
    private final ResourceHolder mResourceHolder;
    private final ServiceRegistry mServiceRegistry;
    private final Supplier<? extends RobotMode> mRobotModeSupplier;
    private final IoInterface mIoInterface;
    private final HidInterface mHidInterface;
    private final Scheduler mScheduler;
    private final Clock mClock;

    public GenericRobotControl(DependencyProvider dependencyProvider) {
        mLogger = dependencyProvider.getLogger();
        mResourceHolder = dependencyProvider.getResourceHolder();
        mServiceRegistry = dependencyProvider.getServiceRegistry();
        mRobotModeSupplier = dependencyProvider.getRobotModeSupplier();
        mIoInterface = dependencyProvider.getIoInterface();
        mHidInterface = dependencyProvider.getHidInterface();
        mScheduler = dependencyProvider.getScheduler();
        mClock = dependencyProvider.getClock();
    }

    @Override
    public final Logger getLogger() {
        return mLogger;
    }

    @Override
    public void registerCloseables(Collection<? extends AutoCloseable> closeables) {
        mResourceHolder.add(closeables);
    }

    @Override
    public void registerService(Service service) {
        mServiceRegistry.add(service);
    }

    @Override
    public final Supplier<? extends RobotMode> getModeSupplier() {
        return mRobotModeSupplier;
    }

    @Override
    public final IoInterface getIoInterface() {
        return mIoInterface;
    }

    @Override
    public final HidInterface getHidInterface() {
        return mHidInterface;
    }

    @Override
    public final Scheduler getScheduler() {
        return mScheduler;
    }

    @Override
    public final Clock getClock() {
        return mClock;
    }
}
