package examples.robot;

import edu.flash3388.flashlib.robot.io.devices.DigitalInput;
import edu.flash3388.flashlib.robot.io.devices.IOFactory;
import edu.flash3388.flashlib.robot.io.devices.PWM;
import edu.flash3388.flashlib.robot.hal.HAL;

/*
 * In this example we will check out usage of the IOFactory to each our 
 * IO port creation. IOFactory is a helper class allowing us to create IO ports
 * for a specific implementation.
 * 
 * For example, if we want to use HAL ports throughout our project, instead of using
 * the HAL classes we will set the IOFactory to an HAL implementation. From IOFactory we will
 * create all necessary ports.
 * The advantage is that if we wanted to change the provider of ports from HAL to something else, 
 * all we need to do is set the provider of IOFactory. So in essence this wraps the creation of 
 * ports for easier use.
 */
public class ExampleIOFactory {

	public static void main(String[] args) {
		//initialize HAL for mode 0. What the mode does depends on the implementation
		//of HAL used. So it's important make sure to find out before using an implementation.
		//
		//initializeHAL does 2 things: loads the native library and initializes HAL.
		//the native library is loaded with a call to System.loadLibrary("flashlib_hal").
		int status = HAL.initializeHAL(0);
		
		//stop the program if initialization failed
		if(status != 0)
			return;

		//new we set the provider of ports to an HAL provider.
		//from this point, we would be able to create IO ports without
		//having to know which implementation is used.
		IOFactory.setProvider(HAL.createIOProvider());
		
		//for example, a PWM port creation.
		PWM pwm = IOFactory.createPWMPort(0);
		
		//or digital input
		DigitalInput input = IOFactory.createDigitalInputPort(1);
		
		
		//shutdown HAL
		HAL.shutdown();
	}
}
