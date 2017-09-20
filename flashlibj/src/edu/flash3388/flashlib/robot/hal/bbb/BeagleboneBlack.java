package edu.flash3388.flashlib.robot.hal.bbb;

import edu.flash3388.flashlib.robot.hal.HALAnalogInput;
import edu.flash3388.flashlib.robot.hal.HALDigitalInput;
import edu.flash3388.flashlib.robot.hal.HALDigitalOutput;
import edu.flash3388.flashlib.robot.hal.HALPWM;

public final class BeagleboneBlack {
	private BeagleboneBlack(){}
	
	public static final int ADC_CHANNEL_COUNT = 7;
	public static final int PWM_MODULE_COUNT = 3;
	public static final int PWM_MODULE_PORT_COUNT = 2;
	public static final int HEADER_PIN_COUNT = 46;
	
	public static final int P8_HEADER = 0;
	public static final int P9_HEADER = 1;
	
	
	public static boolean checkValidDIOPort(int header, int pin){
		if(header != 0 && header != 1)
			return false;
		if(pin <= 0 || pin > HEADER_PIN_COUNT)
			return false;
		//TODO: CONSIDER SPECIAL PORTS
		return true;
	}
	public static boolean checkValidADCChannel(int channel){
		return channel >= 0 && channel < ADC_CHANNEL_COUNT;
	}
	public static boolean checkValidPWMPort(int module, int port){
		return module >= 0 && module < PWM_MODULE_COUNT && port >= 0 && port < PWM_MODULE_PORT_COUNT;
	}
	
	public static int convertPWMToHALPort(int module, int port){
		return module * PWM_MODULE_PORT_COUNT + port;
	}
	public static int convertDIOToHALPort(int header, int pin){
		return header * HEADER_PIN_COUNT + pin;
	}
	
	public static HALDigitalInput createDigitalInputPort(int header, int pin){
		if(!checkValidDIOPort(header, pin))
			return null;
		return new HALDigitalInput(convertDIOToHALPort(header, pin));
	}
	public static HALDigitalOutput createDigitalOutputPort(int header, int pin){
		if(!checkValidDIOPort(header, pin))
			return null;
		return new HALDigitalOutput(convertDIOToHALPort(header, pin));
	}
	
	public static HALPWM createPWMPort(int module, int port){
		if(!checkValidPWMPort(module, port))
			return null;
		return new HALPWM(convertPWMToHALPort(module, port));
	}
	
	public static HALAnalogInput createAnalogInputPort(int channel){
		if(!checkValidADCChannel(channel))
			return null;
		return new HALAnalogInput(channel);
	}
}
