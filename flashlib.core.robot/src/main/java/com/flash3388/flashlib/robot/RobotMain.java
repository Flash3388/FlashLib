package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.app.AppCreator;
import com.flash3388.flashlib.app.FlashLibMain;
import org.slf4j.Logger;

/**
 * <p>
 *     Launcher for robot classes. Robot should be started through here.
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
     * @param logger logger to be used by the robot.
     */
    public static void start(RobotCreator robotCreator, Logger logger) {
        AppCreator appCreator = new RobotAppCreator(robotCreator);
        FlashLibMain.appMain(appCreator, logger);
    }
}
