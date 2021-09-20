package com.flash3388.flashlib.robot.base;

import com.castle.util.dependencies.DependencyContainer;
import com.flash3388.flashlib.hid.HidInterface;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.robot.Managers;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.services.ServiceRegistry;
import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.util.resources.ResourceHolder;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class DependencyProvider {

    private final Managers mManagers;
    private final DependencyContainer mContainer;
    private final Logger mLogger;

    public DependencyProvider(Managers managers, DependencyContainer container, Logger logger) {
        mManagers = managers;
        mContainer = container;
        mLogger = logger;
    }

    public Logger getLogger() {
        return mLogger;
    }

    public ResourceHolder getResourceHolder() {
        return mManagers.getResourceHolder();
    }

    public ServiceRegistry getServiceRegistry() {
        return mManagers.getServiceRegistry();
    }

    public Clock getClock() {
        return mContainer.get(Clock.class);
    }

    public Supplier<? extends RobotMode> getRobotModeSupplier() {
        Supplier<?> foundDependency = null;

        for (Supplier<?> dependency : mContainer.getAllMatching(Supplier.class)) {
            Object value = dependency.get();
            if (value instanceof RobotMode) {
                foundDependency = dependency;
                break;
            }
        }

        if (foundDependency == null) {
            throw new ClassCastException("Didn't find robotmode supplier");
        }

        //noinspection unchecked
        return (Supplier<? extends RobotMode>) foundDependency;
    }

    public IoInterface getIoInterface() {
        return mContainer.get(IoInterface.class);
    }

    public HidInterface getHidInterface() {
        return mContainer.get(HidInterface.class);
    }

    public Scheduler getScheduler() {
        return mContainer.get(Scheduler.class);
    }
}
