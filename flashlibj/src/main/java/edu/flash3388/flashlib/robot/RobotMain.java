package edu.flash3388.flashlib.robot;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>
 *     Launcher for robot classes extending {@link RobotBase}.
 *     Call {@link #start(RobotBase, Logger)}.
 * </p>
 *
 * @since FlashLib 1.3.0
 */
public class RobotMain {

    private RobotMain() {}

    /**
     * <p>
     *     Starts the robot class.
     * </p>
     * <p>
     *     Any exception thrown from the robot class is caught and logged.
     * </p>
     *
     * @param robotBase robot class to launch.
     * @param logger logger to log any errors into.
     */
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
