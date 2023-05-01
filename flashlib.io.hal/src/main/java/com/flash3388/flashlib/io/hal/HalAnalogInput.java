package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.AnalogAccumulator;
import com.flash3388.flashlib.io.AnalogInput;
import hal.HALAIOJNI;
import hal.HALJNI;
import hal.HalConfig;

public class HalAnalogInput extends HalPort implements AnalogInput {


    public HalAnalogInput(long env, String name) {
        super(env, name, HALJNI.HAL_TYPE_ANALOG_INPUT);
    }

    @Override
    public int getValue() {
        return HALAIOJNI.get(mEnv, mHandle);
    }

    @Override
    public double getVoltage() {
        return getValue() / (double) getMaxValue() * getMaxVoltage();
    }

    @Override
    public AnalogAccumulator getAccumulator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getSampleRate() {
        throw new UnsupportedOperationException();
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
