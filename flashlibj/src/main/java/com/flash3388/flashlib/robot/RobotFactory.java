package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.robot.scheduling.SingleThreadScheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.SystemNanoClock;
import org.slf4j.Logger;

public class RobotFactory {

    private RobotFactory() {}

    public static Scheduler newDefaultScheduler() {
        return new SingleThreadScheduler();
    }

    public static Scheduler newDefaultScheduler(Logger logger) {
        return new SingleThreadScheduler(logger);
    }

    public static Clock newDefaultClock() {
        return new SystemNanoClock();
    }
}
