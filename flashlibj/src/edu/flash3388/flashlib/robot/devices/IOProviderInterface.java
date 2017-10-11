package edu.flash3388.flashlib.robot.devices;

public interface IOProviderInterface {

	DigitalInput createDigitalInput(int port);
	DigitalOutput createDigitalOutput(int port);
	
	AnalogInput createAnalogInput(int port);
	AnalogOutput createAnalogOutput(int port);
	
	PWM createPWM(int port);
	
	PulseCounter createPulseCounter(int port);
	PulseCounter createPulseCounter(int upPort, int downPort);
}
