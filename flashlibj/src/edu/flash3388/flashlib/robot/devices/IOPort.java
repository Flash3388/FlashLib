package edu.flash3388.flashlib.robot.devices;

/**
 * Interface for IO ports. Contains a single method {@link #free()} which releases the port from usage.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface IOPort {

	/**
	 * Frees the port. Releasing all resources and
	 * closing it. The port cannot be used after that.
	 */
	void free();
}
