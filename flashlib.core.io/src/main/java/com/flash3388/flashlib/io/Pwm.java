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
     * Sets the pwm output value for the port. This corresponds to
     * a duty cycle value and depends on the used implementation.
     *
     * @param value pwm value
     */
	void setValue(int value);

    /**
     * Gets the current pwm value measured set in the port. This corresponds to
     * a duty cycle value and depends on the used implementation.
     *
     * @return pwm value
     */
	int getValue();

    /**
     * Gets the maximum raw value of the port. This value corresponds to
     * a duty cycle value and depends on the used implementation.
     *
     * @return maximum raw value.
     */
	int getMaxValue();
	
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
