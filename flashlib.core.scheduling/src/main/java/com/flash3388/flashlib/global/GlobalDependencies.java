package com.flash3388.flashlib.global;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import org.slf4j.Logger;

import java.util.concurrent.atomic.AtomicReference;

public class GlobalDependencies {
    private static final AtomicReference<Scheduler> sScheduler = new AtomicReference<>();

    public static Scheduler getScheduler() {
        return sScheduler.get();
    }
    public static void setSchedulerInstance(Scheduler instance) {
        sScheduler.set(instance);
    }

    private static final AtomicReference<Clock> sClock = new AtomicReference<>();

    public static Clock getClock() {
        return sClock.get();
    }
    public static void setClockInstance(Clock instance) {
        sClock.set(instance);
    }

    private static final AtomicReference<Logger> sLogger = new AtomicReference<>();

    public static Logger getLogger() {
        return sLogger.get();
    }
    public static void setLoggerInstance(Logger instance) {
        sLogger.set(instance);
    }
}
