package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.modes.RobotMode;

public interface IterativeRobotControl extends RobotControl {

    @FunctionalInterface
    interface Initializer {
        IterativeRobotControl init(RobotControl robotControl) throws RobotInitializationException;
    }

    void disabledInit();
    void disabledPeriodic();

    void modeInit(RobotMode mode);
    void modePeriodic(RobotMode mode);

    void robotPeriodic();
    void robotStop();
}
