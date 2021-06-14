package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.control.Direction;
import com.flash3388.flashlib.robot.systems.ConstantSpeedMotorSystem;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class ConstantRotateAction extends ActionBase {

    private final ConstantSpeedMotorSystem mSystem;
    private final Direction mDirection;

    public ConstantRotateAction(ConstantSpeedMotorSystem system, Direction direction) {
        mSystem = system;
        mDirection = direction;

        requires(system);
    }

    @Override
    public void execute() {
        mSystem.rotate(mDirection);
    }

    @Override
    public void end(boolean wasInterrupted) {
        mSystem.stop();
    }
}
