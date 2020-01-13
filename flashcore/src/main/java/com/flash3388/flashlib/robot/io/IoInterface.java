package com.flash3388.flashlib.robot.io;

public interface IoInterface {

    AnalogInput newAnalogInput(IoChannel channel);
    AnalogOutput newAnalogOutput(IoChannel channel);
    DigitalInput newDigitalInput(IoChannel channel);
    DigitalOutput newDigitalOutput(IoChannel channel);
    Pwm newPwm(IoChannel channel);
}
