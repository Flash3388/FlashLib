package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.AnalogOutput;
import hal.HALAIOJNI;
import hal.HALJNI;
import hal.HalConfig;

public class HalAnalogOutput extends HalPort implements AnalogOutput {

    public HalAnalogOutput(long env, String name) {
        super(env, name, HALJNI.HAL_TYPE_ANALOG_OUTPUT);
    }

    @Override
    public void setValue(int value) {
        HALAIOJNI.set(mEnv, mHandle, value);
    }

    @Override
    public int getValue() {
        return HALAIOJNI.get(mEnv, mHandle);
    }

    @Override
    public void setVoltage(double voltage) {
        setValue((int) (voltage / getMaxVoltage() * getMaxValue()));
    }

    @Override
    public double getVoltage() {
        return getValue() / (double) getMaxValue() * getMaxVoltage();
    }

    @Override
    public double getMaxVoltage() {
        return HALJNI.getProperty_f(mEnv, mHandle, HalConfig.KEY_ANALOG_MAX_VOLTAGE);
    }

    @Override
    public int getMaxValue() {
        return HALJNI.getProperty(mEnv, mHandle, HalConfig.KEY_ANALOG_MAX_VALUE);
    }
}
