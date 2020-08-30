package com.flash3388.flashlib.robot.control;

import com.flash3388.flashlib.time.Clock;
import com.jmath.ExtendedMath;

import java.util.function.DoubleSupplier;
import java.util.function.DoubleUnaryOperator;

public class ProcessVariableSupplierPidController extends PidController implements DoubleUnaryOperator {

    private final DoubleSupplier mProcessVariable;
    private final Clock mClock;

    public ProcessVariableSupplierPidController(DoubleSupplier kp, DoubleSupplier ki, DoubleSupplier kd, DoubleSupplier kf, DoubleSupplier processVariable, Clock clock) {
        super(kp, ki, kd, kf);
        mProcessVariable = processVariable;
        mClock = clock;
    }

    public ProcessVariableSupplierPidController(double kp, double ki, double kd, double kf, DoubleSupplier processVariable, Clock clock) {
        super(kp, ki, kd, kf);
        mProcessVariable = processVariable;
        mClock = clock;
    }


    @Override
    public double applyAsDouble(double setpoint) {
        return applyAsDouble(mProcessVariable.getAsDouble(), setpoint, mClock.currentTime());
    }

    public boolean hasReached(double setpoint, double margin) {
        double distanceToTarget = Math.abs(mProcessVariable.getAsDouble() - setpoint);
        return ExtendedMath.constrained(distanceToTarget, 0, margin);
    }
}
