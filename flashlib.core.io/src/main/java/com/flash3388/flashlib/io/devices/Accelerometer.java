package com.flash3388.flashlib.io.devices;

import java.io.Closeable;
import java.util.function.DoubleSupplier;

/**
 * Interface for accelerometer sensors. Accelerometers are used for measuring acceleration of the robot.
 *
 * @since FlashLib 3.0.0
 */
public interface Accelerometer extends Closeable, DoubleSupplier {

    /**
     * Gets the acceleration value measured by the sensor in G (9.8 meters per second squared).
     *
     * @return acceleration in G.
     */
    double getAcceleration();

    @Override
    default double getAsDouble() {
        return getAcceleration();
    }
}
