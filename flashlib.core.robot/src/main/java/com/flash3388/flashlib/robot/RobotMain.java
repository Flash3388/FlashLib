package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.base.RobotCreator;
import com.flash3388.flashlib.robot.base.RobotProgram;
import org.slf4j.Logger;

/**
 * <p>
 *     Launcher for robot classes extending {@link RobotControl}.
 *     Call {@link #start(RobotCreator, Logger)}.
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
     * @param robotCreator creator for the robot class.
     * @param logger logger to log any errors into.
     */
    public static void start(RobotCreator robotCreator, Logger logger) {
        RobotProgram robotProgram = new RobotProgram(robotCreator, logger);
        robotProgram.start();
    }
}
