package com.flash3388.flashlib.robot;

import org.slf4j.Logger;

/**
 * <p>
 *     Launcher for robot classes extending {@link Robot}.
 *     Call {@link #start(RobotBaseCreator, Logger)}.
 * </p>
 *
 * @since FlashLib 1.3.0
 */
public final class RobotMain {

    private RobotMain() {}

    /**
     * <p>
     *     Starts the robot class.
     * </p>
     * <p>
     *     Any exception thrown from the robot class is caught and logged.
     * </p>
     *
     * @param robotBaseCreator creator for the robot class.
     * @param logger logger to log any errors into.
     */
    public static void start(RobotBaseCreator robotBaseCreator, Logger logger) {
        RobotProgram robotProgram = new RobotProgram(robotBaseCreator, logger);
        robotProgram.start();
    }
}
