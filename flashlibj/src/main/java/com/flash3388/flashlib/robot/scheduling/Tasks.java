package com.flash3388.flashlib.robot.scheduling;

public class Tasks {

    private Tasks() {}

    public static SchedulerTask repeating(Runnable runnable) {
        return () -> {
            runnable.run();
            return true;
        };
    }

    public static SchedulerTask once(Runnable runnable) {
        return () -> {
            runnable.run();
            return false;
        };
    }
}
