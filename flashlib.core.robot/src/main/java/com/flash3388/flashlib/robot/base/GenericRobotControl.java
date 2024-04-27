package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.app.BasicServiceRegistry;
import com.flash3388.flashlib.app.FlashLibControl;
import com.flash3388.flashlib.app.ServiceRegistry;
import com.flash3388.flashlib.app.concurrent.DefaultFlashLibThreadFactory;
import com.flash3388.flashlib.app.net.NetworkConfiguration;
import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.app.net.NetworkInterfaceImpl;
import com.flash3388.flashlib.app.watchdog.FeedReporter;
import com.flash3388.flashlib.app.watchdog.InternalWatchdog;
import com.flash3388.flashlib.app.watchdog.LoggingFeedReporter;
import com.flash3388.flashlib.app.watchdog.MultiFeedReporters;
import com.flash3388.flashlib.app.watchdog.Watchdog;
import com.flash3388.flashlib.app.watchdog.WatchdogImpl;
import com.flash3388.flashlib.app.watchdog.WatchdogService;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.io.devices.DeviceInterface;
import com.flash3388.flashlib.io.devices.DeviceInterfaceImpl;
import com.flash3388.flashlib.net.hfcs.HfcsRegistry;
import com.flash3388.flashlib.net.obsr.ObjectStorage;
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotFactory;
import com.flash3388.flashlib.robot.hfcs.control.HfcsRobotControl;
import com.flash3388.flashlib.robot.hfcs.control.RobotControlData;
import com.flash3388.flashlib.robot.hfcs.state.HfcsRobotState;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.modes.RobotModeSupplier;
import com.flash3388.flashlib.robot.modes.StaticRobotModeSupplier;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.FlashLibMainThreadImpl;
import com.flash3388.flashlib.util.concurrent.NamedThreadFactory;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenericRobotControl implements RobotControl {

    public static class Configuration {
        final boolean hfcsRobotControl;
        final HidBackend hidBackend;
        final IoBackend ioBackend;

        Configuration(boolean hfcsRobotControl, HidBackend hidBackend, IoBackend ioBackend) {
            this.hfcsRobotControl = hfcsRobotControl;
            this.hidBackend = hidBackend;
            this.ioBackend = ioBackend;
        }

        public static Configuration create(boolean hfcsRobotControl, HidBackend hidBackend, IoBackend ioBackend) {
            return new Configuration(
                    hfcsRobotControl,
                    hidBackend,
                    ioBackend
            );
        }

        public static Configuration controlStubs() {
            return new Configuration(
                    false,
                    HidBackend.STUB,
                    IoBackend.STUB
            );
        }
    }

    private static final Logger LOGGER = Logging.getMainLogger();

    private final InstanceId mInstanceId;
    private final ResourceHolder mResourceHolder;

    private final NamedThreadFactory mThreadFactory;
    private final RobotModeSupplier mModeSupplier;
    private final NetworkInterface mNetworkInterface;
    private final IoInterface mIoInterface;
    private final HidInterface mHidInterface;
    private final Scheduler mScheduler;
    private final Clock mClock;
    private final ServiceRegistry mServiceRegistry;
    private final FlashLibMainThread mMainThread;
    private final DeviceInterface mDeviceInterface;
    private final WatchdogService mWatchdogService;

    public GenericRobotControl(InstanceId instanceId,
                               ResourceHolder resourceHolder,
                               NamedThreadFactory threadFactory,
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
        mThreadFactory = threadFactory;
        mModeSupplier = modeSupplier;
        mNetworkInterface = networkInterface;
        mIoInterface = ioInterface;
        mHidInterface = hidInterface;
        mScheduler = scheduler;
        mClock = clock;
        mServiceRegistry = serviceRegistry;
        mMainThread = mainThread;
        mDeviceInterface = deviceInterface;

        mWatchdogService = new WatchdogService(mThreadFactory);
        mServiceRegistry.register(mWatchdogService);
    }

    public GenericRobotControl(InstanceId instanceId,
                               ResourceHolder resourceHolder,
                               NetworkConfiguration networkConfiguration,
                               RobotModeSupplier modeSupplier,
                               Configuration configuration) {
        mInstanceId = instanceId;
        mResourceHolder = resourceHolder;

        mThreadFactory = new DefaultFlashLibThreadFactory();
        mClock = RobotFactory.newDefaultClock();
        mMainThread = new FlashLibMainThreadImpl();

        mServiceRegistry = new BasicServiceRegistry(mMainThread);
        mNetworkInterface = new NetworkInterfaceImpl(networkConfiguration,
                mInstanceId, mServiceRegistry, mClock,
                mMainThread, mThreadFactory);

        ObjectStorage objectStorage = mNetworkInterface.getMode().isObjectStorageEnabled() ?
                mNetworkInterface.getObjectStorage() :
                new ObjectStorage.Stub();

        if (configuration.hfcsRobotControl) {
            if (!mNetworkInterface.getMode().isHfcsEnabled()) {
                throw new IllegalStateException("HFCS control requested but HFCS not enabled");
            }
            HfcsRegistry hfcsRegistry = mNetworkInterface.getHfcsRegistry();
            Supplier<RobotControlData> controlDataSupplier =
                    HfcsRobotControl.registerReceiver(hfcsRegistry, instanceId);

            if (modeSupplier == null) {
                mModeSupplier = ()-> controlDataSupplier.get().getMode();
            } else {
                mModeSupplier = modeSupplier;
            }

            HfcsRobotState.registerProvider(this, Time.milliseconds(100));
        } else {
            if (modeSupplier == null) {
                mModeSupplier = new StaticRobotModeSupplier(RobotMode.DISABLED);
            } else {
                mModeSupplier = modeSupplier;
            }
        }

        mScheduler = RobotFactory.newDefaultScheduler(mClock, objectStorage, mMainThread);
        mDeviceInterface = new DeviceInterfaceImpl(mMainThread);

        mHidInterface = configuration.hidBackend.createInterface(this);
        mIoInterface = configuration.ioBackend.createInterface(this);

        mWatchdogService = new WatchdogService(mThreadFactory);
        mServiceRegistry.register(mWatchdogService);
    }

    public GenericRobotControl(InstanceId instanceId,
                               ResourceHolder resourceHolder,
                               NetworkConfiguration networkConfiguration,
                               Configuration configuration) {
        this(instanceId, resourceHolder, networkConfiguration, null, configuration);
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
    public Watchdog newWatchdog(String name, Time timeout, FeedReporter reporter) {
        StoredObject rootObject = WatchdogImpl.getWatchdogStoredObject(this, name);
        FeedReporter feedReporter = new MultiFeedReporters(Arrays.asList(new LoggingFeedReporter(), reporter));
        InternalWatchdog watchdog = new WatchdogImpl(getClock(), name, timeout, feedReporter, rootObject);
        mWatchdogService.register(watchdog);

        return watchdog;
    }

    @Override
    public Watchdog newWatchdog(String name, Time timeout) {
        StoredObject rootObject = WatchdogImpl.getWatchdogStoredObject(this, name);
        InternalWatchdog watchdog = new WatchdogImpl(getClock(), name, timeout, new LoggingFeedReporter(), rootObject);
        mWatchdogService.register(watchdog);

        return watchdog;
    }

    @Override
    public NamedThreadFactory getThreadFactory() {
        return mThreadFactory;
    }

    @Override
    public Thread newThread(String name, Consumer<? super FlashLibControl> runnable) {
        return mThreadFactory.newThread(name, ()-> runnable.accept(this));
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return mServiceRegistry;
    }
}
