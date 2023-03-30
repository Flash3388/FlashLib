package com.flash3388.flashlib.robot.modes;

import java.util.function.Supplier;

public interface RobotModeSupplier extends Supplier<RobotMode> {

    @Override
    RobotMode get();

    static RobotModeSupplier of(RobotMode robotMode) {
        return new StaticRobotModeSupplier(robotMode);
    }
}
