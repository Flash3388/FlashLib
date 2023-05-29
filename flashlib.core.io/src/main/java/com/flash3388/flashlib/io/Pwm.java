package com.flash3388.flashlib.io;

/**
 * Interface for PWM ports. This interface is used by devices
 * which require PWM for output, allowing for different implementations.
 *
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
     * Gets the Pwm port output duty cycle.
     *
     * @return duty cycle between 0 and 1.
     */
    double getDuty();
	
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
