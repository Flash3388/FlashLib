package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.motion.Movable2d;
import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.jmath.vectors.Vector2;

import java.util.function.Supplier;

public class Move2dAction extends Action {

    private final Movable2d mMovable;
    private final Supplier<Vector2> mMotionVectorSupplier;

    public Move2dAction(Movable2d movable, Supplier<Vector2> motionVectorSupplier) {
        mMovable = movable;
        mMotionVectorSupplier = motionVectorSupplier;
    }

    @Override
    protected void execute() {
        mMovable.move(mMotionVectorSupplier.get());
    }

    @Override
    protected void end() {
        mMovable.stop();
    }
}
