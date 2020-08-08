package com.flash3388.flashlib.io.devices.pneumatics;

import com.flash3388.flashlib.time.Time;

public interface Solenoid {

    void set(boolean on);
    boolean get();

    void pulse(Time duration);
}
