package com.flash3388.flashlib.robot.control;

import java.util.function.DoubleSupplier;

public class FullySuppliedPidController implements DoubleSupplier {

    private final PartiallySupplierPidController mPidController;
    private final DoubleSupplier mSetpoint;

    public FullySuppliedPidController(PartiallySupplierPidController pidController, DoubleSupplier mSetpoint) {
        this.mPidController = pidController;
        this.mSetpoint = mSetpoint;
    }

    public FullySuppliedPidController(DoubleSupplier kP, DoubleSupplier kI, DoubleSupplier kD, DoubleSupplier kF, DoubleSupplier processVariable, DoubleSupplier setpoint) {
        this(new PartiallySupplierPidController(kP, kI, kD, kF, processVariable), setpoint);
    }

    public FullySuppliedPidController(double kP, double kI, double kD, double kF, DoubleSupplier processVariable, DoubleSupplier setpoint) {
        this(new PartiallySupplierPidController(kP, kI, kD, kF, processVariable), setpoint);
    }

    public boolean hasReached(double margin) {
        return mPidController.hasReached(mSetpoint.getAsDouble(), margin);
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
    public double getAsDouble() {
        return mPidController.applyAsDouble(mSetpoint.getAsDouble());
    }
}
