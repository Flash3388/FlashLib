package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.app.AppCreator;
import com.flash3388.flashlib.app.FlashLibMain;

/**
 * <p>
 *     Launcher for robot classes. Robot should be started through here.
 *     Call {@link #start(RobotCreator)}.
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
     */
    public static void start(RobotCreator robotCreator) {
        AppCreator appCreator = new RobotAppCreator(robotCreator);
        FlashLibMain.appMain(appCreator);
    }
}
