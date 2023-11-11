package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.DigitalInput;
import com.flash3388.flashlib.io.PulseCounter;
import com.flash3388.flashlib.io.PulseLengthCounter;
import com.flash3388.flashlib.io.UnsupportedChannelException;
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

    @Override
    public PulseCounter createCounter() {
        throw new UnsupportedChannelException();
    }

    @Override
    public PulseLengthCounter createLengthCounter() {
        throw new UnsupportedChannelException();
    }
}
