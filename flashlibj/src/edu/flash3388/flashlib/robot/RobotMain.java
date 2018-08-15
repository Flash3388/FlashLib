package edu.flash3388.flashlib.robot;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RobotMain {

    private RobotMain() {}


    public static void start(RobotBase robotBase, Logger logger) {
        try {
            logger.entering("RobotBase", "initialize");
            robotBase.initialize();
        } catch (RobotInitializationException e) {
            logger.log(Level.SEVERE, "Error in robot initialization", e);
            return;
        }

        try {
            logger.entering("RobotBase", "robotMain");
            robotBase.robotMain();
        } catch (Throwable t) {
            logger.log(Level.SEVERE, "Error in robot main", t);
        } finally {
            logger.entering("RobotBase", "shutdown");
            robotBase.robotShutdown();
        }
    }
}