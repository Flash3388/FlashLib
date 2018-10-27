package edu.flash3388.flashlib.robot;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RobotMain {

    private RobotMain() {}

    public static void start(RobotBase robotBase, Logger logger) {
        try {
            logger.entering("RobotBase", "initialize");
            robotBase.initialize();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error in robot initialization", t);
            return;
        }

        try {
            logger.entering("RobotBase", "robotMain");
            robotBase.start();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error in robot main", t);
        } finally {
            logger.entering("RobotBase", "stop");
            robotBase.stop();
        }
    }
}
