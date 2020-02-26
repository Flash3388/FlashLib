package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.modes.RobotMode;

public interface IterativeRobot {

    @FunctionalInterface
    interface Initializer {
        IterativeRobot init(RobotControl robotControl) throws RobotInitializationException;
    }

    void disabledInit();
    void disabledPeriodic();

    void modeInit(RobotMode mode);
    void modePeriodic(RobotMode mode);

    void robotPeriodic();
    void robotStop();
}
