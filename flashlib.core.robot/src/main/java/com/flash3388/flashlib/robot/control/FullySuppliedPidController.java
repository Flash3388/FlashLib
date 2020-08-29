package com.flash3388.flashlib.robot.control;

import java.util.function.DoubleSupplier;

public class FullySuppliedPidController extends ProcessVariableSupplierPidController implements DoubleSupplier {

    private final DoubleSupplier mSetpoint;

    public FullySuppliedPidController(DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf, DoubleSupplier processVariable, DoubleSupplier setpoint) {
        super(kp, ki, kd, kf, processVariable);
        mSetpoint = setpoint;
    }

    public FullySuppliedPidController(double kp, double ki, double kd, double kf, DoubleSupplier processVariable, DoubleSupplier setpoint) {
        super(kp, ki, kd, kf, processVariable);
        mSetpoint = setpoint;
    }

    @Override
    public double getAsDouble() {
        return applyAsDouble(mSetpoint.getAsDouble());
    }
}
