package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.AnalogInput;
import edu.flash3388.flashlib.robot.devices.AnalogOutput;
import edu.flash3388.flashlib.robot.devices.DigitalInput;
import edu.flash3388.flashlib.robot.devices.DigitalOutput;
import edu.flash3388.flashlib.robot.devices.IOProviderInterface;
import edu.flash3388.flashlib.robot.devices.PWM;
import edu.flash3388.flashlib.robot.devices.PulseCounter;

public class HALProvider implements IOProviderInterface{

	@Override
	public DigitalInput createDigitalInput(int port) {
		return new HALDigitalInput(port);
	}
	@Override
	public DigitalOutput createDigitalOutput(int port) {
		return new HALDigitalOutput(port);
	}
	
	@Override
	public AnalogInput createAnalogInput(int port) {
		return new HALAnalogInput(port);
	}
	@Override
	public AnalogOutput createAnalogOutput(int port) {
		return new HALAnalogOutput(port);
	}

	@Override
	public PWM createPWM(int port) {
		return new HALPWM(port);
	}

	@Override
	public PulseCounter createPulseCounter(int port) {
		return new HALPulseCounter(port);
	}
	@Override
	public PulseCounter createPulseCounter(int upPort, int downPort){
		return new HALPulseCounter(upPort, downPort);
	}
}
