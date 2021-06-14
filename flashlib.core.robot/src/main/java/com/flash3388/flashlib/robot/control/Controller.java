package com.flash3388.flashlib.robot.control;

import java.util.function.DoubleBinaryOperator;

public interface Controller extends DoubleBinaryOperator {

    void reset();

    double calculate(double processVariable, double setPoint);

    @Override
    default double applyAsDouble(double left, double right) {
        return calculate(left, right);
    }
}
