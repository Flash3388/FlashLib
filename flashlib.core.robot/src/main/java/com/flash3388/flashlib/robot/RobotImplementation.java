package com.flash3388.flashlib.robot;

import com.flash3388.flashlib.robot.base.RobotBase;

public class RobotImplementation {

    private final RobotControl mRobotControl;
    private final RobotBase mRobotBase;

    public RobotImplementation(RobotControl robotControl, RobotBase robotBase) {
        mRobotControl = robotControl;
        mRobotBase = robotBase;
    }

    public RobotControl getRobotControl() {
        return mRobotControl;
    }

    public RobotBase getRobotBase() {
        return mRobotBase;
    }
}
