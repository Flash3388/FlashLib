package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.global.GlobalDependencies;

import java.util.concurrent.atomic.AtomicReference;

public final class RunningRobot {

    private RunningRobot() {}

    private static final AtomicReference<RobotControl> sControlInstance = new AtomicReference<>(null);

    public static RobotControl getControl() {
        RobotControl control = sControlInstance.get();
        if (control == null) {
            throw new IllegalStateException("no robotcontrol was set");
        }

        return control;
    }

    public static void setControlInstance(RobotControl instance) {
        RobotControl previousInstance = sControlInstance.getAndSet(instance);

        GlobalDependencies.setSchedulerInstance(instance.getScheduler());
        GlobalDependencies.setClockInstance(instance.getClock());
        GlobalDependencies.setLoggerInstance(instance.getLogger());

        if (previousInstance != null) {
            previousInstance.getLogger().warn("RunningRobot instance replaced");
        }
    }
}
