package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.AnalogInput;
import edu.flash3388.flashlib.robot.devices.DigitalOutput;
import edu.flash3388.flashlib.robot.devices.DigitalInput;
import edu.flash3388.flashlib.robot.devices.PWM;
import edu.flash3388.flashlib.robot.devices.PulseCounter;

/**
 * Provides utilities for the Hardware Abstraction Layer implementation 
 * for BeagleBone Black. Using this class you can insure that ports numbers
 * are valid.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public final class BeagleboneBlack {
	private BeagleboneBlack(){}
	
	public static final int ADC_CHANNEL_COUNT = 7;
	public static final int PWM_MODULE_COUNT = 3;
	public static final int PWM_MODULE_PORT_COUNT = 2;
	public static final int HEADER_PIN_COUNT = 46;
	
	public static final int P8_HEADER = 0;
	public static final int P9_HEADER = 1;
	
	public static final int PWMSS_MODULE_0 = 0;
	public static final int PWMSS_MODULE_1 = 1;
	public static final int PWMSS_MODULE_2 = 2;
	
	public static final int PWMSS_PORT_A = 0;
	public static final int PWMSS_PORT_B = 1;
	
	private static int[][] notValidDIOs = {
			{//P8
				1, 2
			},
			{//P9
				1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
				32, 33, 34, 35, 36, 37, 38, 39, 40,
				43, 33, 35, 46
			}
	};
	
	/**
	 * Gets whether or not a BBB port is valid for Digital input or output.
	 * 
	 * 
	 * @param header the header for the pin {@link #P8_HEADER} or {@link #P9_HEADER}
	 * @param pin the pin number on the header
	 * @return true if the given port is valid for DIO.
	 */
	public static boolean checkValidDIOPort(int header, int pin){
		if(header != 0 && header != 1)
			return false;
		if(pin <= 0 || pin > HEADER_PIN_COUNT)
			return false;
		//TODO: CONSIDER SPECIAL PORTS
		for (int i = 0; i < notValidDIOs[header].length; i++) {
			if(pin == notValidDIOs[header][i])
				return false;
		}
		return true;
	}
	/**
	 * Gets whether or not a BBB port is valid for Analog input.
	 * 
	 * @param channel the channel number
	 * @return true if the ADC channel number exists
	 */
	public static boolean checkValidADCChannel(int channel){
		return channel >= 0 && channel < ADC_CHANNEL_COUNT;
	}
	/**
	 * Gets whether or not a BBB port is valid for PWM output.
	 * 
	 * @param module the PWM module number
	 * @param port the PWM port number
	 * @return true if the given port is a valid PWM port
	 */
	public static boolean checkValidPWMPort(int module, int port){
		return module >= 0 && module < PWM_MODULE_COUNT && port >= 0 && port < PWM_MODULE_PORT_COUNT;
	}
	
	/**
	 * Converts a PWM module and port to a usable HAL port number. The returned 
	 * number should be used to initialize the PWM port.
	 * 
	 * @param module PWM module
	 * @param port PWM port
	 * @return a valid HAL port number
	 */
	public static int convertPWMToHALPort(int module, int port){
		return module * PWM_MODULE_PORT_COUNT + port;
	}
	/**
	 * Converts a DIO header and pin to a usable HAL port number. The returned number
	 * should be used to initialize the DIO port.
	 * 
	 * @param header DIO header 
	 * @param pin DIO pin
	 * @return a valid HAL port number
	 */
	public static int convertDIOToHALPort(int header, int pin){
		return (header * HEADER_PIN_COUNT + pin) - 1;
	}
	
	/**
	 * Creates a new Digital input port for the given port. If {@link #checkValidDIOPort(int, int)}
	 * returns false, null is returned. Otherwise the given values are converted to an HAL port number and
	 * a digital input port is created and returned.
	 * 
	 * @param header DIO header
	 * @param pin DIO pin
	 * @return a digital input port, or null if port is not valid
	 */
	public static DigitalInput createDigitalInputPort(int header, int pin){
		if(!checkValidDIOPort(header, pin))
			return null;
		return new HALDigitalInput(convertDIOToHALPort(header, pin));
	}
	/**
	 * Creates a new Digital output port for the given port. If {@link #checkValidDIOPort(int, int)}
	 * returns false, null is returned. Otherwise the given values are converted to an HAL port number and
	 * a digital output port is created and returned.
	 * 
	 * @param header DIO header
	 * @param pin DIO pin
	 * @return a digital output port, or null if port is not valid
	 */
	public static DigitalOutput createDigitalOutputPort(int header, int pin){
		if(!checkValidDIOPort(header, pin))
			return null;
		return new HALDigitalOutput(convertDIOToHALPort(header, pin));
	}
	
	/**
	 * Creates a new PWM port for the given port. If {@link #checkValidPWMPort(int, int)}
	 * returns false, null is returned. Otherwise the given values are converted to an HAL port number and
	 * a PWM port is created and returned.
	 * 
	 * @param module PWM module
	 * @param port PWM port
	 * @return a PWM port, or null if port is not valid
	 */
	public static PWM createPWMPort(int module, int port){
		if(!checkValidPWMPort(module, port))
			return null;
		return new HALPWM(convertPWMToHALPort(module, port));
	}
	
	/**
	 * Creates a new analog input port for the given port. If {@link #checkValidADCChannel(int)}
	 * returns false, null is returned. Otherwise the given values are converted to an HAL port number and
	 * an analog input port is created and returned.
	 * 
	 * @param channel the analog channel
	 * @return an analog input port, or null if port is not valid
	 */
	public static AnalogInput createAnalogInputPort(int channel){
		if(!checkValidADCChannel(channel))
			return null;
		return new HALAnalogInput(channel);
	}
	
	/**
	 * Creates a new pulse counter for a given digital input port. If {@link #checkValidDIOPort(int, int)}
	 * returns false, null is returned. Otherwise the given values are converted to an HAL port number and
	 * an pulse counter is created and returned.
	 * 
	 * @param header DIO header
	 * @param pin DIO pin
	 * @return a pulse counter, or null if port is not valid
	 */
	public static PulseCounter createPulseCounterPort(int header, int pin){
		if(!checkValidDIOPort(header, pin))
			return null;
		return new HALPulseCounter(convertDIOToHALPort(header, pin));
	}
}
