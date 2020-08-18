package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.scheduling.actions.ActionBase;
import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;

public class PidAction extends ActionBase {

    private final PidController mPidController;
    private final Movable mMovable;
    private final DoubleSupplier mProcessVariableSupplier;
    private final DoubleSupplier mSetPointSupplier;
    private final double mThresholdMargin;

    public PidAction(PidController pidController, Movable movable, DoubleSupplier processVariableSupplier, DoubleSupplier setPointSupplier, double thresholdMargin) {
        mPidController = pidController;
        mMovable = movable;
        mProcessVariableSupplier = processVariableSupplier;
        mSetPointSupplier = setPointSupplier;
        mThresholdMargin = thresholdMargin;
    }

    @Override
    public void initialize() {
        mPidController.reset();
    }

    @Override
    public void execute() {
        double value = mPidController.calculate(
                mProcessVariableSupplier.getAsDouble(),
                mSetPointSupplier.getAsDouble());

        mMovable.move(value);
    }

    @Override
    public boolean isFinished() {
        double processVariable = mProcessVariableSupplier.getAsDouble();

        return ExtendedMath.constrained(
                mProcessVariableSupplier.getAsDouble(),
                processVariable - mThresholdMargin,
                processVariable + mThresholdMargin);
    }

    @Override
    public void end(boolean wasInterrupted) {
        mMovable.stop();
    }
}