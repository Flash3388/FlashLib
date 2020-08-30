package com.flash3388.flashlib.robot.systems.valves;

import com.flash3388.flashlib.io.devices.valve.Solenoid;
import com.flash3388.flashlib.io.devices.valve.SolenoidGroup;
import com.flash3388.flashlib.scheduling.Subsystem;

/**
 * A robot system made up of solenoids, controlling a valve.
 *
 * @since FlashLib 3.0.0
 */
public class SolenoidSystem extends Subsystem implements Valve {

    private final Solenoid mSolenoid;

    public SolenoidSystem(Solenoid solenoid) {
        mSolenoid = solenoid;
    }

    public SolenoidSystem(Solenoid... solenoids) {
        this(new SolenoidGroup(solenoids));
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
