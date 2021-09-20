package com.flash3388.flashlib.robot.control;

public interface Controller {

    void reset();

    double calculate(double processVariable, double setPoint);
}
