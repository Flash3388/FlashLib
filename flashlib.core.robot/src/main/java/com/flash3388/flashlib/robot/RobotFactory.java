package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SingleThreadScheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import org.slf4j.Logger;

public final class RobotFactory {

    private RobotFactory() {}

    public static Scheduler newDefaultScheduler(Clock clock) {
        return new SingleThreadScheduler(clock);
    }

    public static Scheduler newDefaultScheduler(Clock clock, Logger logger) {
        return new SingleThreadScheduler(clock, logger);
    }

    public static Clock newDefaultClock() {
        return new SystemNanoClock();
    }
}
