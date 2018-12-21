package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.JavaNanoClock;
import edu.flash3388.flashlib.util.Singleton;

public class RobotResources {

    private RobotResources() {}

    public static final Singleton<Scheduler> SCHEDULER = new Singleton<>(new Scheduler());
    public static final Singleton<Clock> CLOCK = new Singleton<>(new JavaNanoClock());
}
