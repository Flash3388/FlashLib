package com.flash3388.flashlib.robot.modes;

public class StaticRobotModeSupplier implements RobotModeSupplier {

    private final RobotMode mRobotMode;

    public StaticRobotModeSupplier(RobotMode robotMode) {
        mRobotMode = robotMode;
    }

    @Override
    public RobotMode getMode() {
        return mRobotMode;
    }
}
