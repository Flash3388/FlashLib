package com.flash3388.flashlib.app;

import com.flash3388.flashlib.app.net.NetworkConfiguration;
import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.app.net.NetworkInterfaceImpl;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Collection;

public class BasicFlashLibControl implements FlashLibControl {

    private final InstanceId mInstanceId;
    private final ResourceHolder mResourceHolder;
    private final Logger mLogger;

    private final Clock mClock;
    private final ServiceRegistry mServiceRegistry;
    private final NetworkInterface mNetworkInterface;

    public BasicFlashLibControl(InstanceId instanceId, ResourceHolder resourceHolder, Logger logger,
                                NetworkConfiguration networkConfiguration) {
        mInstanceId = instanceId;
        mResourceHolder = resourceHolder;
        mLogger = logger;

        mClock = new SystemNanoClock();
        mServiceRegistry = new BasicServiceRegistry(logger);
        mNetworkInterface = new NetworkInterfaceImpl(
                networkConfiguration, instanceId, mServiceRegistry, mClock, logger);
    }

    public BasicFlashLibControl(InstanceId instanceId, ResourceHolder resourceHolder, Logger logger) {
        this(instanceId, resourceHolder, logger, NetworkConfiguration.disabled());
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
        return mLogger;
    }

    @Override
    public void registerCloseables(Collection<? extends AutoCloseable> closeables) {
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
}
