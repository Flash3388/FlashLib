package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.flash3388.flashlib.time.Time;

import java.util.function.DoubleSupplier;

public class MoveToDistanceAction extends ActionBase {

    private final PidController mPidController;
    private final Movable mMovable;
    private final DoubleSupplier mCurrentPositionSupplier;
    private final double mWantedDistance;

    public MoveToDistanceAction(PidController pidController,
                                Movable movable,
                                DoubleSupplier currentPositionSupplier,
                                double wantedDistance,
                                double distanceMargin,
                                Time toleranceTime) {
        mPidController = pidController;
        mMovable = movable;
        mCurrentPositionSupplier = currentPositionSupplier;
        mWantedDistance = wantedDistance;

        pidController.setTolerance(distanceMargin, toleranceTime);

        requires(movable);
    }

    @Override
    public void initialize() {
        mPidController.reset();
    }

    @Override
    public void execute() {
        double pidResult = mPidController.applyAsDouble(mCurrentPositionSupplier.getAsDouble(), mWantedDistance);
        mMovable.move(pidResult);
    }

    @Override
    public boolean isFinished() {
        return mPidController.atSetpoint(mCurrentPositionSupplier.getAsDouble(), mWantedDistance);
    }

    @Override
    public void end(boolean wasInterrupted) {
        mMovable.stop();
    }
}
