package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.robot.scheduling.Action;

import java.util.function.DoubleSupplier;

public class MoveAction extends Action {

    private final Movable mMovable;
    private final DoubleSupplier mSpeedSupplier;

    public MoveAction(Movable movable, DoubleSupplier speedSupplier) {
        mMovable = movable;
        mSpeedSupplier = speedSupplier;
    }

    @Override
    protected void execute() {
        mMovable.move(mSpeedSupplier.getAsDouble());
    }

    @Override
    protected void end() {
        mMovable.stop();
    }
}
