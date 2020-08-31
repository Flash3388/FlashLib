package com.flash3388.flashlib.robot.control;

import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

public class PartiallySuppliedPidController implements DoubleUnaryOperator {

    private final PidController mPidController;
    private final DoubleSupplier mProcessVariable;

    public PartiallySuppliedPidController(PidController pidController, DoubleSupplier processVariable) {
        mPidController = pidController;
        mProcessVariable = processVariable;
    }

    public PartiallySuppliedPidController(DoubleSupplier kP, DoubleSupplier kI, DoubleSupplier kD, DoubleSupplier kF, DoubleSupplier processVariable) {
        this(new PidController(kP, kI, kD, kF), processVariable);
    }

    public PartiallySuppliedPidController(double kP, double kI, double kD, double kF, DoubleSupplier processVariable) {
        this(new PidController(kP, kI, kD, kF), processVariable);
    }

    public boolean hasReached(double setpoint, double margin) {
        double distanceToTarget = Math.abs(mProcessVariable.getAsDouble() - setpoint);
        return ExtendedMath.constrained(distanceToTarget, 0, margin);
    }

    public void reset() {
        mPidController.reset();
    }

    public void setOutputRampRate(double rate) {
        mPidController.setOutputRampRate(rate);
    }

    public void setSetpointRange(double range) {
        mPidController.setSetpointRange(range);
    }

    public double getMaximumOutput() {
        return mPidController.getMaximumOutput();
    }

    public double getMinimumOutput() {
        return mPidController.getMinimumOutput();
    }

    public void setOutputLimit(double min, double max) {
        mPidController.setOutputLimit(min, max);
    }

    public void setOutputLimit(double outputLimit) {
        mPidController.setOutputLimit(outputLimit);
    }

    @Override
    public double applyAsDouble(double setpoint) {
        return mPidController.applyAsDouble(mProcessVariable.getAsDouble(), setpoint);
    }
}
