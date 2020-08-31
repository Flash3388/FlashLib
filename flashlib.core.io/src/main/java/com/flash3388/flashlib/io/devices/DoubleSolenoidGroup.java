package com.flash3388.flashlib.io.devices;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A group of {@link DoubleSolenoid} controlled as one.
 *
 * @since FlashLib 3.0.0.
 */
public class DoubleSolenoidGroup implements DoubleSolenoid {

    private final Collection<DoubleSolenoid> mSolenoids;

    public DoubleSolenoidGroup(Collection<? extends DoubleSolenoid> solenoids) {
        if (solenoids.isEmpty()) {
            throw new IllegalArgumentException("Expected a non-empty list");
        }
        mSolenoids = new ArrayList<>(solenoids);
    }

    public DoubleSolenoidGroup(DoubleSolenoid... solenoids) {
        this(Arrays.asList(solenoids));
    }

    @Override
    public void set(Value value) {
        for (DoubleSolenoid solenoid : mSolenoids) {
            solenoid.set(value);
        }
    }

    @Override
    public Value get() {
        return mSolenoids.iterator().next().get();
    }
}
