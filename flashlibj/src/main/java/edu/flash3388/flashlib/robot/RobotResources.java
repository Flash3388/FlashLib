package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.util.Singleton;
import edu.flash3388.flashlib.util.resources.ResourceHolder;

import java.util.logging.Logger;

public class RobotResources {

    private RobotResources() {}

    public static final Singleton<Logger> ROBOT_LOGGER = new Singleton<>();
    public static final Singleton<Clock> CLOCK = new Singleton<>();
    public static final Singleton<ResourceHolder> RESOURCE_HOLDER = new Singleton<>();
    public static final Singleton<Scheduler> SCHEDULER = new Singleton<>();

}
