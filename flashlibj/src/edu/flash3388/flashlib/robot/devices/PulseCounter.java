package edu.flash3388.flashlib.robot.devices;

import edu.flash3388.flashlib.util.beans.IntegerSource;

/**
 * Interface for a pulse counter. A pulse counter is used to
 * count pulses from digital input ports, and measure their length.
 * <p>
 * This interface is used by devices which require pulse counting for input, 
 * allowing for different implementations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public interface PulseCounter extends IntegerSource, IOPort{

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
	
	/**
	 * Gets the length of the last pulse counted.
	 * 
	 * @return length of the last pulse in seconds.
	 */
	double getPulseLength();
	
	/**
	 * Gets the time period between the last two pulses in seconds.
	 * 
	 * @return time in seconds between last 2 pulses.
	 */
	double getPulsePeriod();
}
