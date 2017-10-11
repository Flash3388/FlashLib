package edu.flash3388.flashlib.robot.devices;

/**
 * Interface for analog input ports. This interface is used by devices
 * which require analog input ports for input, allowing for different implementations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface AnalogInput extends IOPort{

	/**
	 * Gets the current analog value measured on the port. This corresponds to
	 * a voltage value and depends on the used implementation.
	 * 
	 * @return analog input value
	 */
	int getValue();
	/**
	 * Gets the current analog voltage measured on the port.
	 * 
	 * @return analog input voltage in volts
	 */
	double getVoltage();
	
	/**
	 * Gets the {@link AnalogAccelerometer} accumulator object used by this input port
	 * to accumulate values.
	 * 
	 * @return the accumulator object, or null if none exist.
	 */
	AnalogAccumulator getAccumulator();
	
	/**
	 * Gets the port sample rate in second. This value indicates the period
	 * of value sampling from the port.
	 * 
	 * @return sample rate in seconds
	 */
	double getSampleRate();
	
	/**
	 * Gets the maximum voltage of the analog input port.
	 * 
	 * @return maximum voltage on the port in volts.
	 */
	double getMaxVoltage();
	/**
	 * Gets the maximum raw value of the port. This value corresponds to
	 * a voltage value and depends on the used implementation.
	 * 
	 * @return maximum raw value.
	 */
	int getMaxValue();
}
