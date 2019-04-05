package com.flash3388.flashlib.robot.io;

/**
 * Interface for digital output ports. This interface is used by devices
 * which require digital output ports for output, allowing for different implementations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface DigitalOutput extends IoPort {

	/**
	 * Gets the current digital input value of the port. If the
	 * port reads HIGH true is returned, false otherwise.
	 * 
	 * @return true if current input is digital HIGH, false otherwise
	 */
	boolean get();
	
	/**
	 * Sets the current digital output value of the port. 
	 * 
	 * @param high digital output value: true for HIGH, false for low.
	 */
	void set(boolean high);
	/**
	 * Sets the digital port to HIGH output for a given time, creating
	 * a digital pulse.
	 * 
	 * @param length the amount of time to set high in seconds, i.e. the pulse length 
	 */
	void pulse(double length);
}
