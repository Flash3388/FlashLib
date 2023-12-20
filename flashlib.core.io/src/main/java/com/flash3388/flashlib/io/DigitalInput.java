package com.flash3388.flashlib.io;

/**
 * Interface for digital input ports. This interface is used by devices
 * which require digital input ports for input, allowing for different implementations.
 *
 * @since FlashLib 1.2.0
 */
public interface DigitalInput extends IoPort {

	/**
	 * Gets the current digital input value of the port. If the
	 * port reads HIGH true is returned, false otherwise.
	 * 
	 * @return true if current input is digital HIGH, false otherwise
	 */
	boolean get();

	/**
	 * Creates a new {@link PulseCounter} associated with this port.
	 * The counter will count pulses from the port.
	 * <p>
	 * Depending on the underlying implementation, multiple counters (of the same
	 * or different types) might not be supported.
	 *
	 * @return {@link PulseCounter}
	 * @throws UnsupportedChannelException if the port does not support this counter
	 */
	PulseCounter createCounter();

	/**
	 * Creates a new {@link PulseLengthCounter} associated with this port.
	 * The counter will measure the length of pulses received by the port.
	 * <p>
	 * Depending on the underlying implementation, multiple counters (of the same
	 * or different types) might not be supported.
	 *
	 * @return {@link PulseLengthCounter}
	 * @throws UnsupportedChannelException if the port does not support this counter
	 */
	PulseLengthCounter createLengthCounter();
}
