package com.flash3388.flashlib.io.devices.pneumatics;

public interface DoubleSolenoid {

    enum Value {
        FORWARD,
        REVERSE,
        OFF
    }

    void set(Value value);
    Value get();
}
