package com.flash3388.flashlib;

import com.flash3388.flashlib.robot.RunningRobot;
import org.slf4j.Logger;

public class Debug {

    private Debug() {}

    public static Logger getLogger() {
        return RunningRobot.INSTANCE.get().getLogger();
    }
}
