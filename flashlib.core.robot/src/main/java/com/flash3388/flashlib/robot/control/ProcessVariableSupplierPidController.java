package com.flash3388.flashlib.robot.control;

import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

public class ProcessVariableSupplierPidController extends PidController implements DoubleUnaryOperator {

    private final DoubleSupplier mProcessVariable;

    public ProcessVariableSupplierPidController(DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf, DoubleSupplier processVariable) {
        super(kp, ki, kd, kf);
        mProcessVariable = processVariable;
    }

    public ProcessVariableSupplierPidController(double kp, double ki, double kd, double kf, DoubleSupplier processVariable) {
        super(kp, ki, kd, kf);
        mProcessVariable = processVariable;
    }


    @Override
    public double applyAsDouble(double setpoint) {
        return applyAsDouble(mProcessVariable.getAsDouble(), setpoint);
    }

    public boolean hasReached(double setpoint, double margin) {
        double distanceToTarget = Math.abs(mProcessVariable.getAsDouble() - setpoint);
        return ExtendedMath.constrained(distanceToTarget, 0, margin);
    }
}
