package edu.flash3388.flashlib.robot.frc.io;

import edu.flash3388.flashlib.robot.io.AnalogInput;
import edu.flash3388.flashlib.robot.io.AnalogOutput;
import edu.flash3388.flashlib.robot.io.DigitalInput;
import edu.flash3388.flashlib.robot.io.DigitalOutput;
import edu.flash3388.flashlib.robot.io.IOProvider;
import edu.flash3388.flashlib.robot.io.PWM;
import edu.flash3388.flashlib.robot.io.devices.PulseCounter;
import edu.flash3388.flashlib.robot.frc.io.devices.FRCPWM;
import edu.flash3388.flashlib.robot.frc.io.devices.FRCPulseCounter;

public class FRCIOProvider implements IOProvider{

	@Override
	public DigitalInput createDigitalInput(int port) {
		return new FRCDigitalInput(port);
	}
	@Override
	public DigitalOutput createDigitalOutput(int port) {
		return new FRCDigitalOutput(port);
	}

	@Override
	public AnalogInput createAnalogInput(int port) {
		return new FRCAnalogInput(port);
	}
	@Override
	public AnalogOutput createAnalogOutput(int port) {
		return new FRCAnalogOutput(port);
	}

	@Override
	public PWM createPWM(int port) {
		return new FRCPWM(port);
	}

	@Override
	public PulseCounter createPulseCounter(int port) {
		return new FRCPulseCounter(port, false);
	}
	@Override
	public PulseCounter createPulseCounter(int upPort, int downPort) {
		return new FRCPulseCounter(upPort, downPort);
	}
}
