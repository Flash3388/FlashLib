package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.global.GlobalDependencies;

import java.util.concurrent.atomic.AtomicReference;

public final class RunningRobot {

    private RunningRobot() {}

    private static final AtomicReference<RobotControl> sInstance = new AtomicReference<>(null);

    public static RobotControl getInstance() {
        return sInstance.get();
    }

    public static void setInstance(RobotControl instance) {
        RobotControl previousInstance = sInstance.getAndSet(instance);

        GlobalDependencies.setSchedulerInstance(instance.getScheduler());
        GlobalDependencies.setClockInstance(instance.getClock());

        if (previousInstance != null) {
            previousInstance.getLogger().warn("RunningRobot instance replaced");
        }
    }
}
