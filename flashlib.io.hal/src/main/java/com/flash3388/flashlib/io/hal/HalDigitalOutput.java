package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.DigitalOutput;
import hal.HALDIOJNI;
import hal.HALJNI;

public class HalDigitalOutput extends HalPort implements DigitalOutput {


    public HalDigitalOutput(long env, String name) {
        super(env, name, HALJNI.HAL_TYPE_DIGITAL_OUTPUT);
    }

    @Override
    public boolean get() {
        return HALDIOJNI.get(mEnv, mHandle);
    }

    @Override
    public void set(boolean high) {
        HALDIOJNI.set(mEnv, mHandle, high);
    }

    @Override
    public void pulse(double length) {
        throw new UnsupportedOperationException();
    }
}
