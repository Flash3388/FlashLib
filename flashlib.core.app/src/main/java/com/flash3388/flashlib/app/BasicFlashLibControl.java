package com.flash3388.flashlib.app;

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
import com.flash3388.flashlib.net.obsr.StoredObject;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
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

public class BasicFlashLibControl implements FlashLibControl {

    private static final Logger LOGGER = Logging.getMainLogger();

    private final InstanceId mInstanceId;
    private final ResourceHolder mResourceHolder;

    private final NamedThreadFactory mThreadFactory;
    private final Clock mClock;
    private final ServiceRegistry mServiceRegistry;
    private final NetworkInterface mNetworkInterface;
    private final FlashLibMainThread mMainThread;
    private final WatchdogService mWatchdogService;

    public BasicFlashLibControl(InstanceId instanceId,
                                ResourceHolder resourceHolder,
                                NetworkConfiguration networkConfiguration) {
        mInstanceId = instanceId;
        mResourceHolder = resourceHolder;

        mThreadFactory = new DefaultFlashLibThreadFactory();
        mMainThread = new FlashLibMainThreadImpl();
        mClock = new SystemNanoClock();
        mServiceRegistry = new BasicServiceRegistry(mMainThread);
        mNetworkInterface = new NetworkInterfaceImpl(
                networkConfiguration, instanceId, mServiceRegistry, mClock, mMainThread, mThreadFactory);

        mWatchdogService = new WatchdogService(mThreadFactory);
        mServiceRegistry.register(mWatchdogService);
    }

    public BasicFlashLibControl(InstanceId instanceId, ResourceHolder resourceHolder) {
        this(instanceId, resourceHolder, NetworkConfiguration.disabled());
    }

    @Override
    public InstanceId getInstanceId() {
        return mInstanceId;
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
        mMainThread.verifyCurrentThread();
        mResourceHolder.add(closeables);
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return mServiceRegistry;
    }

    @Override
    public NetworkInterface getNetworkInterface() {
        return mNetworkInterface;
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
}
