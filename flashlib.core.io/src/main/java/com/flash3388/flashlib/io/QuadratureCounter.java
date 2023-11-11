package com.flash3388.flashlib.io;

/**
 * Interface for a quadrature pulse counter. Counts and measures
 * pulses received on two input ports.
 *
 * @since FlashLib 3.3.0
 */
public interface QuadratureCounter extends PulseCounter {

    /**
     * Gets the time period between the last two pulses in seconds.
     *
     * @return time in seconds between last 2 pulses.
     */
    double getPulsePeriod();

    /**
     * Gets which channel last sent a pulse.
     *
     * @return true if up channel pulsed last, false if the down channel did.
     */
    boolean getDirection();
}
