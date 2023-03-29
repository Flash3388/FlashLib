package com.flash3388.flashlib.robot.modes;

import com.flash3388.flashlib.annotations.MainThreadOnly;

import java.util.function.Supplier;

public interface RobotModeSupplier extends Supplier<RobotMode> {

    @MainThreadOnly
    @Override
    RobotMode get();

    static RobotModeSupplier of(RobotMode robotMode) {
        return new StaticRobotModeSupplier(robotMode);
    }
}
