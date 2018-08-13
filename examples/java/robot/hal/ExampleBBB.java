package examples.robot.hal;

import static edu.flash3388.flashlib.robot.hal.BeagleboneBlack.*;

import edu.flash3388.flashlib.robot.io.devices.AnalogInput;
import edu.flash3388.flashlib.robot.io.devices.DigitalOutput;
import edu.flash3388.flashlib.robot.io.devices.PWM;
import edu.flash3388.flashlib.robot.hal.HAL;

/*
 * In this example we will review using the FlashLib Hardware Abstraction Layer witg
 * a BeagleboneBlack implementation.
 * 
 * The reason there is a difference here is that FlashLib provides an helper class for
 * using a BBB implementation.
 * 
 * Note the static import of the BeagleboneBlack class. This class has static method which will help 
 * us with port creation, insuring the we are using valid ports.
 */
public class ExampleBBB {

	public static void main(String[] args) {
		//initialize HAL for mode 0. In the BBB implementations, modes are ignored so
		//the given value does not matter.
		int status = HAL.initializeHAL(0);
		
		//If HAL was successfully initialized
		if(status == 0){
			
			//creating a digital output port using the BBB helper class
			//the port is located on the P8 header and is port number 1.
			DigitalOutput outputPort = createDigitalOutputPort(P8_HEADER, 1);
			//set our output port to HIGH
			outputPort.set(true);
			
			//check if the given channel is valid for analog input
			//if it is, create an analog input port
			if(checkValidADCChannel(5)){
				AnalogInput analogInput = createAnalogInputPort(5);
				//read input voltage
				double volts = analogInput.getVoltage();
			}
			
			//create a PWM port for module 0, port A.
			PWM pwmPort = createPWMPort(PWMSS_MODULE_0, PWMSS_PORT_A);
			//set the PWM output duty
			pwmPort.setDuty(1.0);
			
			//shutdown HAL, freeing all used ports.
			HAL.shutdown();
		}
	}
}
