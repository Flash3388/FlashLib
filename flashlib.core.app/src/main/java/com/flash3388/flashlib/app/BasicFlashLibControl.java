package com.flash3388.flashlib.app;

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

    public BasicFlashLibControl(InstanceId instanceId, ResourceHolder resourceHolder, Logger logger) {
        mInstanceId = instanceId;
        mResourceHolder = resourceHolder;
        mLogger = logger;

        mClock = new SystemNanoClock();
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
}
