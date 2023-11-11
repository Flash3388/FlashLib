package com.flash3388.flashlib.io;

import java.io.Closeable;

/**
 * Interface for a pulse counter. A pulse counter is used to
 * count pulses from digital input ports.
 *
 * @since FlashLib 1.0.2
 */
public interface PulseCounter extends Closeable {

	/**
	 * Resets the counter, setting the pulse count to zero.
	 */
	void reset();
	
	/**
	 * Gets the amount of pulses counted by the pulse counter.
	 * 
	 * @return amount of pulses counted.
	 */
	int get();
}
