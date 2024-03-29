package com.flash3388.flashlib.robot.hfcs.state;

import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.time.Time;

import java.util.function.Supplier;

public class StateDataSupplier implements Supplier<RobotStateData> {

    private final RobotControl mControl;

    public StateDataSupplier(RobotControl control) {
        mControl = control;
    }

    @Override
    public RobotStateData get() {
        RobotMode currentMode = mControl.getMode();
        Time clockTime = mControl.getClock().currentTime();

        return new RobotStateData(currentMode, clockTime);
    }
}
