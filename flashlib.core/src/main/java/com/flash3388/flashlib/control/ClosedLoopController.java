package com.flash3388.flashlib.control;

import java.util.function.DoubleBinaryOperator;

public interface ClosedLoopController extends DoubleBinaryOperator {

    /**
     * Resets the controller.
     */
    void reset();

    /**
     * Calculates the output to the system to compensate for the error.
     *
     * @param processVariable the process variable of the system.
     * @param setpoint the desired set point.
     *
     * @return the compensation value from the loop calculation
     */
    @Override
    double applyAsDouble(double processVariable, double setpoint);

    /**
     * Determines whether the current error can be acceptably considered as within the setpoint range.
     *
     * @param processVariable the process variable of the system.
     * @param setpoint the desired set point.
     *
     * @return <b>true</b> if the error can be acceptably considered as within the setpoint range, <b>false</b> otherwise.
     */
    boolean isInTolerance(double processVariable, double setpoint);
}
