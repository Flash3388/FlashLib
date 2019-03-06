package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.JavaNanoClock;
import com.flash3388.flashlib.time.Clock;

public class RobotFactory {

    private RobotFactory() {}

    public static Scheduler newDefaultScheduler() {
        return new Scheduler();
    }

    public static Clock newDefaultClock() {
        return new JavaNanoClock();
    }
}
