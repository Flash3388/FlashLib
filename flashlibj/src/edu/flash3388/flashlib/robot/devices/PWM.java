package edu.flash3388.flashlib.robot.devices;

/**
 * Interface for PWM ports. This interface is used by devices
 * which require analog PWM for output, allowing for different implementations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface PWM extends IOPort{

	/**
	 * Sets the PWM port output duty cycle.
	 * 
	 * @param duty duty cycle between 0 and 1.
	 */
	void setDuty(double duty);
	/**
	 * Sets the PWM port output raw value. This corresponds to
	 * a duty cycle value and depends on the used implementation.
	 * 
	 * @param raw raw PWM output value
	 */
	void setRaw(int raw);
	
	/**
	 * Gets the PWM port output duty cycle.
	 * 
	 * @return duty cycle between 0 and 1.
	 */
	double getDuty();
	/**
	 * Gets the PWM port output raw value. This corresponds to
	 * a duty cycle value and depends on the used implementation.
	 * 
	 * @return raw PWM output value
	 */
	int getRaw();
	
	/**
	 * Sets the PWM port's frequency.
	 * 
	 * @param frequency frequency in HZ.
	 */
	void setFrequency(double frequency);
	/**
	 * Gets the PWM port's frequency.
	 * 
	 * @return frequency in HZ.
	 */
	double getFrequency();
}
