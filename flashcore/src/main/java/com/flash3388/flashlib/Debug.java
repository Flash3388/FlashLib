package com.flash3388.flashlib;

import com.flash3388.flashlib.robot.RunningRobot;
import org.slf4j.Logger;

public final class Debug {

    private Debug() {}

    public static Logger getLogger() {
        return RunningRobot.getInstance().getLogger();
    }

    public static void print(String msg) {
        getLogger().debug(msg);
    }

    public static void print(String msg, Object... args) {
        getLogger().debug(msg, args);
    }

    public static void print(String msg, Throwable throwable) {
        getLogger().debug(msg, throwable);
    }
}
