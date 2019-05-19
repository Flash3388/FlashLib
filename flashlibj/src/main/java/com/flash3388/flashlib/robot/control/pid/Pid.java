package com.flash3388.flashlib.robot.control.pid;

public interface Pid {

    double getP();
    double getI();
    double getD();

    void setP(double p);
    void setI(double i);
    void setD(double d);

    void setOutputLimit(double min, double max);
}
