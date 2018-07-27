package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.hal.devices.HALPulseCounter;
import edu.flash3388.flashlib.robot.io.AnalogInput;
import edu.flash3388.flashlib.robot.io.AnalogOutput;
import edu.flash3388.flashlib.robot.io.DigitalInput;
import edu.flash3388.flashlib.robot.io.DigitalOutput;
import edu.flash3388.flashlib.robot.io.IOProvider;
import edu.flash3388.flashlib.robot.io.PWM;
import edu.flash3388.flashlib.robot.io.devices.PulseCounter;

public class HALIOProvider implements IOProvider{

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
