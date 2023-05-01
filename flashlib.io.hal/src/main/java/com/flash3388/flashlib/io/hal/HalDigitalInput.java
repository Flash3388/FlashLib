package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.DigitalInput;
import hal.HALDIOJNI;
import hal.HALJNI;

public class HalDigitalInput extends HalPort implements DigitalInput {

    public HalDigitalInput(long env, String name) {
        super(env, name, HALJNI.HAL_TYPE_DIGITAL_INPUT);
    }

    @Override
    public boolean get() {
        return HALDIOJNI.get(mEnv, mHandle);
    }
}
