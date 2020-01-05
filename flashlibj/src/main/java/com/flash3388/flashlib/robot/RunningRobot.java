package com.flash3388.flashlib.robot;

import java.util.concurrent.atomic.AtomicReference;

public final class RunningRobot {

    private RunningRobot() {}

    private static final AtomicReference<Robot> sInstance = new AtomicReference<>(null);

    public static Robot getInstance() {
        return sInstance.get();
    }

    public static void setInstance(Robot instance) {
        Robot previousInstance = sInstance.getAndSet(instance);
        if (previousInstance != null) {
            previousInstance.getLogger().warn("RunningRobot instance replaced");
        }
    }
}
