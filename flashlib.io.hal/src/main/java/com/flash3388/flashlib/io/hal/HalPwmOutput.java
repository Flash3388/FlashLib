package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.Pwm;
import hal.HALJNI;
import hal.HALPWMJNI;

public class HalPwmOutput extends HalPort implements Pwm {

    public HalPwmOutput(long env, String name) {
        super(env, name, HALJNI.HAL_TYPE_PWM_OUTPUT);
    }

    @Override
    public void setDuty(double duty) {
        HALPWMJNI.setDutyCycle(mEnv, mHandle, (float) duty);
    }

    @Override
    public double getDuty() {
        return HALPWMJNI.getDutyCycle(mEnv, mHandle);
    }

    @Override
    public void setFrequency(double frequency) {
        HALPWMJNI.setFrequency(mEnv, mHandle, (float) frequency);
    }

    @Override
    public double getFrequency() {
        return HALPWMJNI.getFrequency(mEnv, mHandle);
    }
}
