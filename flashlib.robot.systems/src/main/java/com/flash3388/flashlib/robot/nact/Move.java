package com.flash3388.flashlib.robot.nact;

import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;

public class Move extends ActionBase {

    private final Movable mMovable;
    private final DoubleSupplier mSpeed;

    public Move(Movable movable, DoubleSupplier speed) {
        mMovable = movable;
        mSpeed = speed;
    }

    @Override
    public void execute() {
        mMovable.move(mSpeed.getAsDouble());
    }

    @Override
    public void end(boolean wasInterrupted) {
        mMovable.stop();
    }
}
