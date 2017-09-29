package edu.flash3388.flashlib.robot.devices;

/**
 * Interface for analog input ports. This interface is used by devices
 * which require analog input ports for input, allowing for different implementations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface AnalogInput {

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
}
