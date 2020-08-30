package com.flash3388.flashlib.robot.systems.valves;

import com.flash3388.flashlib.io.devices.valve.DoubleSolenoid;
import com.flash3388.flashlib.io.devices.valve.DoubleSolenoidGroup;
import com.flash3388.flashlib.scheduling.Subsystem;

/**
 * A robot system made up of double-solenoids, controlling a valve.
 *
 * @since FlashLib 3.0.0
 */
public class DoubleSolenoidSystem extends Subsystem implements Valve {

    private final DoubleSolenoid mDoubleSolenoid;

    public DoubleSolenoidSystem(DoubleSolenoid doubleSolenoid) {
        mDoubleSolenoid = doubleSolenoid;
    }

    public DoubleSolenoidSystem(DoubleSolenoid... doubleSolenoids) {
        this(new DoubleSolenoidGroup(doubleSolenoids));
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
