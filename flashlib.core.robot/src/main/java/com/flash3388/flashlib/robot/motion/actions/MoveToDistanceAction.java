package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;

public class MoveToDistanceAction extends ActionBase {

    private final PidController mPidController;
    private final Movable mMovable;
    private final DoubleSupplier mDistanceSupplier;
    private final double mWantedDistance;
    private final double mDistanceMargin;

    public MoveToDistanceAction(PidController pidController, Movable movable, DoubleSupplier distanceSupplier, double wantedDistance, double distanceMargin) {
        mPidController = pidController;
        mMovable = movable;
        mDistanceSupplier = distanceSupplier;
        mWantedDistance = wantedDistance;
        mDistanceMargin = distanceMargin;

        requires(movable);
    }

    @Override
    public void initialize() {
        mPidController.reset();
    }

    @Override
    public void execute() {
        double pidResult = mPidController.applyAsDouble(mDistanceSupplier.getAsDouble(), mWantedDistance);
        mMovable.move(pidResult);
    }

    @Override
    public boolean isFinished() {
        double distanceToTarget = Math.abs(mDistanceSupplier.getAsDouble() - mWantedDistance);
        return ExtendedMath.constrained(distanceToTarget, 0, mDistanceMargin);
    }

    @Override
    public void end(boolean wasInterrupted) {
        mMovable.stop();
    }
}
