package com.flash3388.flashlib.robot.systems.pneumatics;

import com.flash3388.flashlib.io.devices.valve.DoubleSolenoid;
import com.flash3388.flashlib.scheduling.Subsystem;

public class DoubleSolenoidSystem extends Subsystem implements Piston {

    private final DoubleSolenoid mDoubleSolenoid;

    public DoubleSolenoidSystem(DoubleSolenoid doubleSolenoid) {
        mDoubleSolenoid = doubleSolenoid;
    }

    @Override
    public void open() {
        mDoubleSolenoid.set(DoubleSolenoid.Value.FORWARD);
    }

    @Override
    public void close() {
        mDoubleSolenoid.set(DoubleSolenoid.Value.REVERSE);
    }

    @Override
    public boolean isOpen() {
        return mDoubleSolenoid.get() == DoubleSolenoid.Value.FORWARD;
    }

    public void off() {
        mDoubleSolenoid.set(DoubleSolenoid.Value.OFF);
    }
}
