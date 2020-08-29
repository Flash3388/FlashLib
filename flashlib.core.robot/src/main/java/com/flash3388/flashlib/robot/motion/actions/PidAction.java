package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.control.FullySuppliedPidController;
import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;

public class PidAction extends ActionBase {

    private final FullySuppliedPidController mPidController;
    private final Movable mMovable;
    private final double mThresholdMargin;

    public PidAction(FullySuppliedPidController pidController, Movable movable, double thresholdMargin) {
        mPidController = pidController;
        mMovable = movable;
        mThresholdMargin = thresholdMargin;

        requires(movable);
    }

    @Override
    public void initialize() {
        mPidController.reset();
    }

    @Override
    public void execute() {
        mMovable.move(mPidController.getAsDouble());
    }

    @Override
    public boolean isFinished() {
        return mPidController.hasReached(mThresholdMargin);
    }

    @Override
    public void end(boolean wasInterrupted) {
        mMovable.stop();
    }
}
