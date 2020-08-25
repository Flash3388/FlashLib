package com.flash3388.flashlib.robot.motion.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;

public class MoveAction extends ActionBase {

    private final Movable mMovable;
    private final DoubleSupplier mSpeedSupplier;

    public MoveAction(Movable movable, DoubleSupplier speedSupplier) {
        mMovable = movable;
        mSpeedSupplier = speedSupplier;
    }

    public MoveAction(Movable movable, double speed) {
        this(movable, Suppliers.of(speed));
    }

    @Override
    public void execute() {
        mMovable.move(mSpeedSupplier.getAsDouble());
    }

    @Override
    public void end(boolean wasInterrupted) {
        mMovable.stop();
    }
}
