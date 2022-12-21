package com.flash3388.flashlib.app;

import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Collection;

public class DelegatingAppBase implements FlashLibControl {

    private final FlashLibControl mControl;

    public DelegatingAppBase(FlashLibControl control) {
        mControl = control;
    }

    @Override
    public final InstanceId getInstanceId() {
        return mControl.getInstanceId();
    }

    @Override
    public final Clock getClock() {
        return mControl.getClock();
    }

    @Override
    public final Logger getLogger() {
        return mControl.getLogger();
    }

    @Override
    public final void registerCloseables(Collection<? extends AutoCloseable> closeables) {
        mControl.registerCloseables(closeables);
    }

    @Override
    public ServiceRegistry getServiceRegistry() {
        return mControl.getServiceRegistry();
    }
}
