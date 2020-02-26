package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.modes.RobotMode;

public interface IterativeRobot extends Robot {

    @FunctionalInterface
    interface Initializer {
        IterativeRobot init(Robot robot) throws RobotInitializationException;
    }

    void disabledInit();
    void disabledPeriodic();

    void modeInit(RobotMode mode);
    void modePeriodic(RobotMode mode);

    void robotPeriodic();
    void robotStop();
}
