package com.flash3388.flashlib.robot.io.devices.pneumatics;

public interface DoubleSolenoid {

    enum Value {
        FORWARD,
        REVERSE,
        OFF
    }

    void set(Value value);
    Value get();
}
