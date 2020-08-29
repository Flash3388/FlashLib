package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.control.ProcessVariableSupplierPidController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;

public class MoveToDistanceAction extends ActionBase {

    private final ProcessVariableSupplierPidController mPidController;
    private final Movable mMovable;
    private final double mWantedDistance;
    private final double mDistanceMargin;

    public MoveToDistanceAction(ProcessVariableSupplierPidController pidController, Movable movable, double wantedDistance, double distanceMargin) {
        mPidController = pidController;
        mMovable = movable;
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
        double pidResult = mPidController.applyAsDouble(mWantedDistance);
        mMovable.move(pidResult);
    }

    @Override
    public boolean isFinished() {
        return mPidController.hasReached(mWantedDistance, mDistanceMargin);
    }

    @Override
    public void end(boolean wasInterrupted) {
        mMovable.stop();
    }
}
