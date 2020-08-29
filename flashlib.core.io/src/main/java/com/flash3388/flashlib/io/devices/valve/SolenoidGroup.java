package com.flash3388.flashlib.io.devices.valve;

import com.flash3388.flashlib.time.Time;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A group of {@link Solenoid} controlled as one.
 *
 * @since FlashLib 3.0.0.
 */
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
    public void set(boolean open) {
        for (Solenoid solenoid : mSolenoids) {
            solenoid.set(open);
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
