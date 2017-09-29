package edu.flash3388.flashlib.robot.devices;

/**
 * Interface for digital input ports. This interface is used by devices
 * which require digital input ports for input, allowing for different implementations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface DigitalInput {

	/**
	 * Gets the current digital input value of the port. If the
	 * port reads HIGH true is returned, false otherwise.
	 * 
	 * @return true if current input is digital HIGH, false otherwise
	 */
	boolean get();
}
