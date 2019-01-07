package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.Singleton;

public class RunningRobot {

    private RunningRobot() {}

    public static final Singleton<Robot> INSTANCE = new Singleton<>();
}
