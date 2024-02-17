package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

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
                                double velocityTolerance) {
        mPidController = pidController;
        mMovable = movable;
        mCurrentPositionSupplier = currentPositionSupplier;
        mWantedDistance = wantedDistance;

        pidController.setTolerance(distanceMargin, velocityTolerance);

        requires(movable);
    }

    @Override
    public void initialize(ActionControl control) {
        mPidController.reset();
    }

    @Override
    public void execute(ActionControl control) {
        double pidResult = mPidController.applyAsDouble(mCurrentPositionSupplier.getAsDouble(), mWantedDistance);
        mMovable.move(pidResult);

        if (mPidController.isInTolerance()) {
            control.finish();
        }
    }

    @Override
    public void end(FinishReason reason) {
        mMovable.stop();
    }
}
