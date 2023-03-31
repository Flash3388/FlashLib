package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.app.BasicServiceRegistry;
import com.flash3388.flashlib.app.net.NetworkConfiguration;
import com.flash3388.flashlib.app.net.NetworkInterfaceImpl;
import com.flash3388.flashlib.io.devices.DeviceInterface;
import com.flash3388.flashlib.io.devices.DeviceInterfaceImpl;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.hfcs.ping.HfcsPing;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.robot.hfcs.control.HfcsRobotControl;
import com.flash3388.flashlib.robot.hfcs.control.RobotControlData;
import com.flash3388.flashlib.robot.hfcs.hid.HfcsHid;
import com.flash3388.flashlib.robot.hfcs.state.HfcsRobotState;
import com.flash3388.flashlib.robot.modes.RobotModeSupplier;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.FlashLibMainThreadImpl;
import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.modes.StaticRobotModeSupplier;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.function.Supplier;

public class GenericRobotControl implements RobotControl {

    public static class Configuration {
        final boolean fullHfcsControl;

        private Configuration(boolean fullHfcsControl) {
            this.fullHfcsControl = fullHfcsControl;
        }

        public static Configuration fullHfcsControl() {
            return new Configuration(
                    true
            );
        }

        public static Configuration controlStubs() {
            return new Configuration(
                    false
            );
        }
    }

    private static final Logger LOGGER = Logging.getMainLogger();

    private final InstanceId mInstanceId;
    private final ResourceHolder mResourceHolder;

    private final RobotModeSupplier mModeSupplier;
    private final NetworkInterface mNetworkInterface;
    private final IoInterface mIoInterface;
    private final HidInterface mHidInterface;
    private final Scheduler mScheduler;
    private final Clock mClock;
    private final ServiceRegistry mServiceRegistry;
    private final FlashLibMainThread mMainThread;
    private final DeviceInterface mDeviceInterface;

    public GenericRobotControl(InstanceId instanceId,
                               ResourceHolder resourceHolder,
                               RobotModeSupplier modeSupplier,
                               NetworkInterface networkInterface,
                               IoInterface ioInterface,
                               HidInterface hidInterface,
                               Scheduler scheduler,
                               Clock clock,
                               ServiceRegistry serviceRegistry,
                               FlashLibMainThread mainThread,
                               DeviceInterface deviceInterface) {
        mInstanceId = instanceId;
        mResourceHolder = resourceHolder;
        mModeSupplier = modeSupplier;
        mNetworkInterface = networkInterface;
        mIoInterface = ioInterface;
        mHidInterface = hidInterface;
        mScheduler = scheduler;
        mClock = clock;
        mServiceRegistry = serviceRegistry;
        mMainThread = mainThread;
        mDeviceInterface = deviceInterface;
    }

    public GenericRobotControl(InstanceId instanceId,
                               ResourceHolder resourceHolder,
                               NetworkConfiguration networkConfiguration,
                               Configuration configuration) {
        mInstanceId = instanceId;
        mResourceHolder = resourceHolder;

        mClock = RobotFactory.newDefaultClock();
        mMainThread = new FlashLibMainThreadImpl();

        mServiceRegistry = new BasicServiceRegistry(mMainThread);
        mNetworkInterface = new NetworkInterfaceImpl(networkConfiguration,
                mInstanceId, mServiceRegistry, mClock,
                mMainThread);

        ObjectStorage objectStorage = mNetworkInterface.getMode().isObjectStorageEnabled() ?
                mNetworkInterface.getObjectStorage() :
                new ObjectStorage.Stub();

        if (configuration.fullHfcsControl) {
            HfcsRegistry hfcsRegistry = mNetworkInterface.getHfcsRegistry();
            HfcsPing.registerReceiver(hfcsRegistry, (a, b)->{});
            Supplier<RobotControlData> controlDataSupplier =
                    HfcsRobotControl.registerReceiver(hfcsRegistry, instanceId);

            mModeSupplier = ()-> controlDataSupplier.get().getMode();
            mHidInterface = HfcsHid.createReceiver(hfcsRegistry, mMainThread);

            HfcsRobotState.registerProvider(this, Time.milliseconds(100));
        } else {
            mModeSupplier = new StaticRobotModeSupplier(RobotMode.DISABLED);
            mHidInterface = new HidInterface.Stub();
        }

        mIoInterface = new IoInterface.Stub();
        mScheduler = RobotFactory.newDefaultScheduler(mClock, objectStorage, mMainThread);
        mDeviceInterface = new DeviceInterfaceImpl(mMainThread);
    }

    public GenericRobotControl(InstanceId instanceId, ResourceHolder resourceHolder, Configuration configuration) {
        this(instanceId, resourceHolder, NetworkConfiguration.disabled(), configuration);
    }

    public GenericRobotControl(InstanceId instanceId, ResourceHolder resourceHolder) {
        this(instanceId, resourceHolder, Configuration.controlStubs());
    }

    @Override
    public InstanceId getInstanceId() {
        return mInstanceId;
    }

    @Override
    public RobotModeSupplier getModeSupplier() {
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
        return LOGGER;
    }

    @Override
    public void registerCloseables(Collection<? extends AutoCloseable> closeables) {
        getMainThread().verifyCurrentThread();
        mResourceHolder.add(closeables);
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        return mNetworkInterface;
    }

    @Override
    public DeviceInterface getDeviceInterface() {
        return mDeviceInterface;
    }

    @Override
    public FlashLibMainThread getMainThread() {
        return mMainThread;
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return mServiceRegistry;
    }
}
