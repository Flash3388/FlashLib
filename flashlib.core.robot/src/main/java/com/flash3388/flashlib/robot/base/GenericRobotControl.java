package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.app.BasicServiceRegistry;
import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.modes.StaticRobotModeSupplier;
import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.function.Supplier;

public class GenericRobotControl implements RobotControl {

    private final InstanceId mInstanceId;
    private final ResourceHolder mResourceHolder;
    private final Logger mLogger;

    private final Supplier<? extends RobotMode> mModeSupplier;
    private final NetworkInterface mNetworkInterface;
    private final IoInterface mIoInterface;
    private final HidInterface mHidInterface;
    private final Scheduler mScheduler;
    private final Clock mClock;
    private final ServiceRegistry mServiceRegistry;

    public GenericRobotControl(InstanceId instanceId, ResourceHolder resourceHolder, Logger logger,
                               Supplier<? extends RobotMode> modeSupplier,
                               NetworkInterface networkInterface,
                               IoInterface ioInterface,
                               HidInterface hidInterface,
                               Scheduler scheduler,
                               Clock clock,
                               ServiceRegistry serviceRegistry) {
        mInstanceId = instanceId;
        mResourceHolder = resourceHolder;
        mLogger = logger;
        mModeSupplier = modeSupplier;
        mNetworkInterface = networkInterface;
        mIoInterface = ioInterface;
        mHidInterface = hidInterface;
        mScheduler = scheduler;
        mClock = clock;
        mServiceRegistry = serviceRegistry;
    }

    public GenericRobotControl(InstanceId instanceId, ResourceHolder resourceHolder, Logger logger) {
        mInstanceId = instanceId;
        mResourceHolder = resourceHolder;
        mLogger = logger;
        mModeSupplier = new StaticRobotModeSupplier(RobotMode.DISABLED);
        mIoInterface = new IoInterface.Stub();
        mHidInterface = new HidInterface.Stub();
        mClock = RobotFactory.newDefaultClock();

        mNetworkInterface = RobotFactory.disabledNetworkInterface();
        mScheduler = RobotFactory.newDefaultScheduler(mClock, logger);
        mServiceRegistry = new BasicServiceRegistry(logger);
    }

    @Override
    public InstanceId getInstanceId() {
        return mInstanceId;
    }

    @Override
    public Supplier<? extends RobotMode> getModeSupplier() {
        return mModeSupplier;
    }

    @Override
    public IoInterface getIoInterface() {
        return mIoInterface;
    }

    @Override
    public HidInterface getHidInterface() {
        return mHidInterface;
    }

    @Override
    public Scheduler getScheduler() {
        return mScheduler;
    }

    @Override
    public Clock getClock() {
        return mClock;
    }

    @Override
    public Logger getLogger() {
        return mLogger;
    }

    @Override
    public void registerCloseables(Collection<? extends AutoCloseable> closeables) {
        mResourceHolder.add(closeables);
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        return mNetworkInterface;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return mServiceRegistry;
    }
}
