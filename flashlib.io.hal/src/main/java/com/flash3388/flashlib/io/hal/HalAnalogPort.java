package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.AnalogPort;
import hal.HALJNI;
import hal.HalConfig;

public class HalAnalogPort extends HalPort implements AnalogPort {

    protected HalAnalogPort(long env, String name, int type) {
        super(env, name, type);
    }

    @Override
    public double getMaxVoltage() {
        return HALJNI.getProperty2(mEnv, mHandle, HalConfig.KEY_ANALOG_MAX_VOLTAGE);
    }

    @Override
    public int getMaxValue() {
        return HALJNI.getProperty(mEnv, mHandle, HalConfig.KEY_ANALOG_MAX_VALUE);
    }
}
