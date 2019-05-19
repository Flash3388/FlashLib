package com.flash3388.flashlib.robot.control.pid;

import com.beans.DoubleProperty;
import com.beans.properties.SimpleDoubleProperty;

public abstract class PidBase implements Pid {

    private final DoubleProperty mKp;
    private final DoubleProperty mKi;
    private final DoubleProperty mKd;

    protected PidBase(DoubleProperty kp, DoubleProperty ki, DoubleProperty kd) {
        mKp = kp;
        mKi = ki;
        mKd = kd;
    }

    protected PidBase(double kp, double ki, double kd) {
        this(new SimpleDoubleProperty(kp), new SimpleDoubleProperty(ki), new SimpleDoubleProperty(kd));
    }

    public DoubleProperty kpProperty() {
        return mKp;
    }

    @Override
    public void setP(double p) {
        mKp.setAsDouble(p);
    }

    @Override
    public double getP() {
        return mKp.getAsDouble();
    }

    public DoubleProperty kiProperty() {
        return mKi;
    }

    @Override
    public void setI(double i) {
        mKi.setAsDouble(i);
    }

    @Override
    public double getI() {
        return mKi.getAsDouble();
    }

    public DoubleProperty kdProperty() {
        return mKd;
    }

    @Override
    public void setD(double d) {
        mKd.setAsDouble(d);
    }

    @Override
    public double getD() {
        return mKd.getAsDouble();
    }

    @Override
    public void setOutputLimit(double min, double max) {
        throw new UnsupportedOperationException();
    }
}
