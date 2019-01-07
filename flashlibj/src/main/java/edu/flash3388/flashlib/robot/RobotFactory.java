package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.JavaNanoClock;

public class RobotFactory {

    private RobotFactory() {}

    public static Scheduler newDefaultScheduler() {
        return new Scheduler();
    }

    public static Clock newDefaultClock() {
        return new JavaNanoClock();
    }
}
