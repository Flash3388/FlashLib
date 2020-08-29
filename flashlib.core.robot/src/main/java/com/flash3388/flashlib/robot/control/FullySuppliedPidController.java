package com.flash3388.flashlib.robot.control;

import com.flash3388.flashlib.time.Clock;

import java.util.function.DoubleSupplier;

public class FullySuppliedPidController extends ProcessVariableSupplierPidController implements DoubleSupplier {

    private final DoubleSupplier mSetpoint;

    public FullySuppliedPidController(DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf, DoubleSupplier processVariable, DoubleSupplier setpoint, Clock clock) {
        super(kp, ki, kd, kf, processVariable, clock);
        mSetpoint = setpoint;
    }

    public FullySuppliedPidController(double kp, double ki, double kd, double kf, DoubleSupplier processVariable, DoubleSupplier setpoint, Clock clock) {
        super(kp, ki, kd, kf, processVariable, clock);
        mSetpoint = setpoint;
    }

    @Override
    public double getAsDouble() {
        return applyAsDouble(mSetpoint.getAsDouble());
    }

    public boolean hasReached(double margin) {
        return hasReached(mSetpoint.getAsDouble(), margin);
    }
}
