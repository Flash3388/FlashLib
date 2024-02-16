package com.flash3388.flashlib.app;

import com.flash3388.flashlib.app.net.NetworkInterface;
import com.flash3388.flashlib.app.watchdog.FeedReporter;
import com.flash3388.flashlib.app.watchdog.Watchdog;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.FlashLibMainThread;
import com.flash3388.flashlib.util.unique.InstanceId;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.function.Consumer;

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

    @Override
    public NetworkInterface getNetworkInterface() {
        return mControl.getNetworkInterface();
    }

    @Override
    public FlashLibMainThread getMainThread() {
        return mControl.getMainThread();
    }

    @Override
    public Watchdog newWatchdog(String name, Time timeout, FeedReporter reporter) {
        return mControl.newWatchdog(name, timeout, reporter);
    }

    @Override
    public Watchdog newWatchdog(String name, Time timeout) {
        return mControl.newWatchdog(name, timeout);
    }

    @Override
    public Thread newThread(String name, Consumer<? super FlashLibControl> runnable) {
        return mControl.newThread(name, runnable);
    }
}
