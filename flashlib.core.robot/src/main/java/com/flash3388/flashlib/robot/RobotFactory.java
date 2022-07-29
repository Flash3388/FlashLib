package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.scheduling.Scheduler;
import com.flash3388.flashlib.scheduling.SynchronousScheduler;
import com.flash3388.flashlib.scheduling.impl.NewSynchronousScheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import com.flash3388.flashlib.util.logging.Logging;
import org.slf4j.Logger;

public final class RobotFactory {

    private RobotFactory() {}

    public static Scheduler newDefaultScheduler(Clock clock) {
        return new NewSynchronousScheduler(clock, Logging.stub());
    }

    public static Scheduler newDefaultScheduler(Clock clock, Logger logger) {
        return new NewSynchronousScheduler(clock, logger);
    }

    public static Clock newDefaultClock() {
        return new SystemNanoClock();
    }
}
