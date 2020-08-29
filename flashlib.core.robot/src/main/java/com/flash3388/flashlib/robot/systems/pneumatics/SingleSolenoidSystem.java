package com.flash3388.flashlib.robot.systems.pneumatics;

import com.flash3388.flashlib.io.devices.valve.Solenoid;
import com.flash3388.flashlib.scheduling.Subsystem;

public class SingleSolenoidSystem extends Subsystem implements Piston {

    private final Solenoid mSolenoid;

    public SingleSolenoidSystem(Solenoid solenoid) {
        mSolenoid = solenoid;
    }

    @Override
    public void open() {
        mSolenoid.set(true);
    }

    @Override
    public void close() {
        mSolenoid.set(false);
    }

    @Override
    public boolean isOpen() {
        return mSolenoid.get();
    }
}
