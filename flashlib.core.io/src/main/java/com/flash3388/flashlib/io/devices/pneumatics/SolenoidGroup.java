package com.flash3388.flashlib.io.devices.pneumatics;

import com.flash3388.flashlib.time.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class SolenoidGroup implements Solenoid {

    private final Collection<Solenoid> mSolenoids;

    public SolenoidGroup(Collection<? extends Solenoid> solenoids) {
        if (solenoids.isEmpty()) {
            throw new IllegalArgumentException("Expected a non-empty list");
        }
        mSolenoids = new ArrayList<>(solenoids);
    }

    public SolenoidGroup(Solenoid... solenoids) {
        this(Arrays.asList(solenoids));
    }

    @Override
    public void set(boolean on) {
        for (Solenoid solenoid : mSolenoids) {
            solenoid.set(on);
        }
    }

    @Override
    public boolean get() {
        return mSolenoids.iterator().next().get();
    }

    @Override
    public void pulse(Time duration) {
        for (Solenoid solenoid : mSolenoids) {
            solenoid.pulse(duration);
        }
    }
}
