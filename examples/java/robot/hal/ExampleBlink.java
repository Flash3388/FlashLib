package examples.robot.hal;

import edu.flash3388.flashlib.robot.devices.DigitalOutput;
import edu.flash3388.flashlib.robot.hal.HAL;
import edu.flash3388.flashlib.robot.hal.HALDigitalOutput;
import edu.flash3388.flashlib.util.FlashUtil;

/*
 * In this example we will look at using the FlashLib Hardware Abstraction Layer.
 * 
 * We will blink a LED connected to port 0 of the platform used. Which port is considered
 * 0 depends on both the implementation and the board used, so check both of those before 
 * writing code with HAL.
 * 
 * Before running HAL code, it is necessary to make sure the HAL native library is in the
 * JVM native library path "java.library.path".
 */
public class ExampleBlink {

	public static void main(String[] args) {
		//initialize HAL for mode 0. What the mode does depends on the implementation
		//of HAL used. So it's important make sure to find out before using an implementation.
		//
		//initializeHAL does 2 things: loads the native library and initializes HAL.
		//the native library is loaded with a call to System.loadLibrary("flashlib_hal").
		int status = HAL.initializeHAL(0);
		
		//If HAL was successfully initialized
		if(status == 0){
		
			//create a digital output for DIO port 0.
			DigitalOutput output = new HALDigitalOutput(0);
			
			/*
			 * With HAL, we can use 2 ways to blink:
			 * - Manual setting output to HIGH, sleeping and setting to LOW.
			 * - Creating a pulse on the port for a given amount of time
			 */
			
			//method 1: manual
			
			//set the output to HIGH
			output.set(true);
			//sleep for 1 second
			FlashUtil.delay(1000);
			//set output to LOW
			output.set(false);
			//sleep for 1 second
			FlashUtil.delay(1000);
			
			//method 2: pulse
			
			//set a pulse, 1 second long for the port
			output.pulse(1.0);
			//sleep for 1 second, waiting for the pulse to end
			FlashUtil.delay(1000);
			
			
			//shutdown HAL, freeing all used ports.
			HAL.shutdown();
		}
	}
}
