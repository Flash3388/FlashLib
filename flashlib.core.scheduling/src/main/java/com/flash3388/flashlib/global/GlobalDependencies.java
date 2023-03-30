package com.flash3388.flashlib.global;

import com.flash3388.flashlib.scheduling.Scheduler;

import java.util.concurrent.atomic.AtomicReference;

public class GlobalDependencies {
    private static final AtomicReference<Scheduler> sScheduler = new AtomicReference<>();

    public static Scheduler getScheduler() {
        return sScheduler.get();
    }
    public static void setSchedulerInstance(Scheduler instance) {
        sScheduler.set(instance);
    }
}
