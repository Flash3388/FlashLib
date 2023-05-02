package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.AnalogOutput;
import hal.HALAIOJNI;
import hal.HALJNI;

public class HalAnalogOutput extends HalAnalogPort implements AnalogOutput {

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
        setValue(voltsToValue(voltage));
    }

    @Override
    public double getVoltage() {
        return valueToVolts(getValue());
    }
}
