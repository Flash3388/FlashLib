package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.motion.Movable2d;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.jmath.vectors.Vector2;

import java.util.function.Supplier;

public class Move2dAction extends ActionBase {

    private final Movable2d mMovable;
    private final Supplier<? extends Vector2> mMotionVectorSupplier;

    public Move2dAction(Movable2d movable, Supplier<? extends Vector2> motionVectorSupplier) {
        mMovable = movable;
        mMotionVectorSupplier = motionVectorSupplier;
    }

    @Override
    public void execute() {
        mMovable.move(mMotionVectorSupplier.get());
    }

    @Override
    public void end(boolean wasInterrupted) {
        mMovable.stop();
    }
}
