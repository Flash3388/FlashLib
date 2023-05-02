package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.AnalogInput;
import hal.HALAIOJNI;
import hal.HALJNI;
import hal.HalConfig;

public class HalAnalogInput extends HalAnalogPort implements AnalogInput {


    public HalAnalogInput(long env, String name) {
        super(env, name, HALJNI.HAL_TYPE_ANALOG_INPUT);
    }

    @Override
    public int getValue() {
        return HALAIOJNI.get(mEnv, mHandle);
    }

    @Override
    public double getVoltage() {
        return valueToVolts(getValue());
    }

    @Override
    public double getSampleRate() {
        return HALJNI.getProperty2(mEnv, mHandle, HalConfig.KEY_ANALOG_SAMPLE_RATE);
    }
}
