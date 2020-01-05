package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.util.Singleton;

public final class RunningRobot {

    private RunningRobot() {}

    public static final Singleton<Robot> INSTANCE = new Singleton<>();
}
