package com.flash3388.flashlib.robot.modes;

import java.util.function.Supplier;

public class StaticRobotModeSupplier implements Supplier<RobotMode> {

    private final RobotMode mRobotMode;

    public StaticRobotModeSupplier(RobotMode robotMode) {
        mRobotMode = robotMode;
    }

    @Override
    public RobotMode get() {
        return mRobotMode;
    }
}
