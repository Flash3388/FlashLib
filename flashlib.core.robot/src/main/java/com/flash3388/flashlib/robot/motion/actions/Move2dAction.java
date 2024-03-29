package com.flash3388.flashlib.robot.motion.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.motion.Movable2d;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.jmath.vectors.Vector2;

import java.util.function.Supplier;

public class Move2dAction extends ActionBase {

    private final Movable2d mMovable;
    private final Supplier<? extends Vector2> mMotionVectorSupplier;

    public Move2dAction(Movable2d movable, Supplier<? extends Vector2> motionVectorSupplier) {
        mMovable = movable;
        mMotionVectorSupplier = motionVectorSupplier;

        requires(movable);
    }

    public Move2dAction(Movable2d movable, Vector2 vector) {
        this(movable, Suppliers.of(vector));
    }

    @Override
    public void initialize(ActionControl control) {

    }

    @Override
    public void execute(ActionControl control) {
        mMovable.move(mMotionVectorSupplier.get());
    }

    @Override
    public void end(FinishReason reason) {
        mMovable.stop();
    }
}
