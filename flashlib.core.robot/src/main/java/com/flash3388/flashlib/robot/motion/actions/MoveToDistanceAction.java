package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.control.PartiallySupplierPidController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

public class MoveToDistanceAction extends ActionBase {

    private final PartiallySupplierPidController mPidController;
    private final Movable mMovable;
    private final double mWantedDistance;
    private final double mDistanceMargin;

    public MoveToDistanceAction(PartiallySupplierPidController pidController, Movable movable, double wantedDistance, double distanceMargin) {
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
