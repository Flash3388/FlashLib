package com.flash3388.flashlib.robot.systems.pneumatics;

import com.flash3388.flashlib.robot.io.devices.pneumatics.Solenoid;
import com.flash3388.flashlib.robot.scheduling.Subsystem;

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
