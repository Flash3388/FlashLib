package com.flash3388.flashlib.io;

/**
 * Interface for analog output ports. This interface is used by devices
 * which require analog output ports for output, allowing for different implementations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public interface AnalogOutput extends IoPort {

	/**
	 * Sets the analog output value for the port. This corresponds to
	 * a voltage value and depends on the used implementation.
	 * 
	 * @param value analog output value
	 */
	void setValue(int value);

    /**
     * Gets the current analog value measured on the port. This corresponds to
     * a voltage value and depends on the used implementation.
     *
     * @return analog input value
     */
    int getValue();

	/**
	 * Sets the analog output voltage for the port.
	 * 
	 * @param voltage analog output voltage in volts
	 */
	void setVoltage(double voltage);

	/**
	 * Gets the current analog voltage measured on the port.
	 * 
	 * @return analog input voltage in volts
	 */
	double getVoltage();
}
