package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.systems.Movable;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
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
    public void initialize(ActionControl control) {

    }

    @Override
    public void execute(ActionControl control) {
        mMovable.move(mSpeed.getAsDouble());
    }

    @Override
    public void end(FinishReason reason) {
        mMovable.stop();
    }
}
