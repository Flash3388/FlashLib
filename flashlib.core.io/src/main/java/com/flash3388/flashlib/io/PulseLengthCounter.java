package com.flash3388.flashlib.io;

import java.io.Closeable;

/**
 * Interface for pulse length counter. Measures the length of pulses
 * received on a port.
 *
 * @since FlashLib 3.3.0
 */
public interface PulseLengthCounter extends Closeable {

    /**
     * Gets the length of the last measured pulse.
     *
     * @return length in seconds.
     */
    double getLengthSeconds();
}
