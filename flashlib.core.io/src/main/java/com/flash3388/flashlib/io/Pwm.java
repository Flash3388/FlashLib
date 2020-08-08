package com.flash3388.flashlib.io;

/**
 * Interface for Pwm ports. This interface is used by devices
 * which require analog Pwm for output, allowing for different implementations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface Pwm extends IoPort {

	/**
	 * Sets the Pwm port output duty cycle.
	 * 
	 * @param duty duty cycle between 0 and 1.
	 */
	void setDuty(double duty);
	/**
	 * Sets the Pwm port output raw value. This corresponds to
	 * a duty cycle value and depends on the used implementation.
	 * 
	 * @param raw raw Pwm output value
	 */
	void setRaw(int raw);
	
	/**
	 * Gets the Pwm port output duty cycle.
	 * 
	 * @return duty cycle between 0 and 1.
	 */
	double getDuty();
	/**
	 * Gets the Pwm port output raw value. This corresponds to
	 * a duty cycle value and depends on the used implementation.
	 * 
	 * @return raw Pwm output value
	 */
	int getRaw();
	
	/**
	 * Sets the Pwm port's frequency.
	 * 
	 * @param frequency frequency in HZ.
	 */
	void setFrequency(double frequency);
	/**
	 * Gets the Pwm port's frequency.
	 * 
	 * @return frequency in HZ.
	 */
	double getFrequency();
}
