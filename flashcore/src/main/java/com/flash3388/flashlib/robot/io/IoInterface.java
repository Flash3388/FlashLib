package com.flash3388.flashlib.robot.io;

public interface IoInterface {

    AnalogInput newAnalogInput(IoChannel channel);
    AnalogOutput newAnalogOutput(IoChannel channel);
    DigitalInput newDigitalInput(IoChannel channel);
    DigitalOutput newDigitalOutput(IoChannel channel);
    Pwm newPwm(IoChannel channel);

    class Stub implements IoInterface {

        @Override
        public AnalogInput newAnalogInput(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public AnalogOutput newAnalogOutput(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public DigitalInput newDigitalInput(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public DigitalOutput newDigitalOutput(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }

        @Override
        public Pwm newPwm(IoChannel channel) {
            throw new UnsupportedOperationException("stub");
        }
    }
}
