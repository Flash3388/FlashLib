package com.flash3388.flashlib.app;

import com.flash3388.flashlib.app.net.NetworkConfiguration;
import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.app.net.NetworkInterfaceImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.FlashLibMainThreadImpl;
import com.flash3388.flashlib.util.logging.Logging;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Collection;

public class BasicFlashLibControl implements FlashLibControl {

    private static final Logger LOGGER = Logging.getMainLogger();

    private final InstanceId mInstanceId;
    private final ResourceHolder mResourceHolder;

    private final Clock mClock;
    private final ServiceRegistry mServiceRegistry;
    private final NetworkInterface mNetworkInterface;
    private final FlashLibMainThread mMainThread;

    public BasicFlashLibControl(InstanceId instanceId,
                                ResourceHolder resourceHolder,
                                NetworkConfiguration networkConfiguration) {
        mInstanceId = instanceId;
        mResourceHolder = resourceHolder;

        mMainThread = new FlashLibMainThreadImpl();
        mClock = new SystemNanoClock();
        mServiceRegistry = new BasicServiceRegistry(mMainThread);
        mNetworkInterface = new NetworkInterfaceImpl(
                networkConfiguration, instanceId, mServiceRegistry, mClock, mMainThread);
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
}
