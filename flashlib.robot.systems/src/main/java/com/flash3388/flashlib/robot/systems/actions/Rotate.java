package com.flash3388.flashlib.robot.systems.actions;

import com.flash3388.flashlib.robot.motion.Rotatable;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;

public class Rotate extends ActionBase {

    private final Rotatable mRotatable;
    private final DoubleSupplier mSpeed;

    public Rotate(Rotatable rotatable, DoubleSupplier speed) {
        mRotatable = rotatable;
        mSpeed = speed;
    }

    @Override
    public void execute(ActionControl control) {
        mRotatable.rotate(mSpeed.getAsDouble());
    }

    @Override
    public void end(FinishReason reason) {
        mRotatable.stop();
    }
}
