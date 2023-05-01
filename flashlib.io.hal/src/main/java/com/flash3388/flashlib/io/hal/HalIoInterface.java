package com.flash3388.flashlib.io.hal;

import com.flash3388.flashlib.io.AnalogInput;
import com.flash3388.flashlib.io.AnalogOutput;
import com.flash3388.flashlib.io.DigitalInput;
import com.flash3388.flashlib.io.DigitalOutput;
import com.flash3388.flashlib.io.IoChannel;
import com.flash3388.flashlib.io.IoInterface;
import com.flash3388.flashlib.io.Pwm;

public class HalIoInterface implements IoInterface {

    private final long mEnv;

    public HalIoInterface(long env) {
        mEnv = env;
    }

    public HalIoInterface() {
        this(HalIo.getEnv());
    }

    @Override
    public AnalogInput newAnalogInput(IoChannel channel) {
        HalIoChannel ioChannel = IoChannel.cast(channel, HalIoChannel.class);
        return new HalAnalogInput(mEnv, ioChannel.getName());
    }

    @Override
    public AnalogOutput newAnalogOutput(IoChannel channel) {
        HalIoChannel ioChannel = IoChannel.cast(channel, HalIoChannel.class);
        return new HalAnalogOutput(mEnv, ioChannel.getName());
    }

    @Override
    public DigitalInput newDigitalInput(IoChannel channel) {
        HalIoChannel ioChannel = IoChannel.cast(channel, HalIoChannel.class);
        return new HalDigitalInput(mEnv, ioChannel.getName());
    }

    @Override
    public DigitalOutput newDigitalOutput(IoChannel channel) {
        HalIoChannel ioChannel = IoChannel.cast(channel, HalIoChannel.class);
        return new HalDigitalOutput(mEnv, ioChannel.getName());
    }

    @Override
    public Pwm newPwm(IoChannel channel) {
        throw new UnsupportedOperationException();
    }
}
