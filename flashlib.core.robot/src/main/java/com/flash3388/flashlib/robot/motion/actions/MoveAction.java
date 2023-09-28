package com.flash3388.flashlib.robot.motion.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;

public class MoveAction extends ActionBase {

    private final Movable mMovable;
    private final DoubleSupplier mSpeedSupplier;

    public MoveAction(Movable movable, DoubleSupplier speedSupplier) {
        mMovable = movable;
        mSpeedSupplier = speedSupplier;

        requires(movable);
    }

    public MoveAction(Movable movable, double speed) {
        this(movable, Suppliers.of(speed));
    }

    @Override
    public void initialize(ActionControl control) {

    }

    @Override
    public void execute(ActionControl control) {
        mMovable.move(mSpeedSupplier.getAsDouble());
    }

    @Override
    public void end(FinishReason reason) {
        mMovable.stop();
    }
}
