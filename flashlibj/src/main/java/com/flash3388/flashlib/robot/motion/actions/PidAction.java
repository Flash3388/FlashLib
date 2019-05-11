package com.flash3388.flashlib.robot.motion.actions;

import com.flash3388.flashlib.robot.control.PidController;
import com.flash3388.flashlib.robot.control.PidProcessType;
import com.flash3388.flashlib.robot.motion.Movable;
import com.flash3388.flashlib.robot.scheduling.Action;
import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;

public class PidAction extends Action {

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
    protected void initialize() {
        mPidController.reset();
    }

    @Override
    protected void execute() {
        double value = mPidController.calculate(
                mProcessVariableSupplier.getAsDouble(),
                mSetPointSupplier.getAsDouble(),
                PidProcessType.DISPLACEMENT);

        mMovable.move(value);
    }

    @Override
    protected boolean isFinished() {
        double processVariable = mProcessVariableSupplier.getAsDouble();

        return ExtendedMath.constrained(
                mProcessVariableSupplier.getAsDouble(),
                processVariable - mThresholdMargin,
                processVariable + mThresholdMargin);
    }

    @Override
    protected void end() {
        mMovable.stop();
    }
}
