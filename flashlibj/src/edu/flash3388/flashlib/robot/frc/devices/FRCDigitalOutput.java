package edu.flash3388.flashlib.robot.frc.devices;

import edu.flash3388.flashlib.robot.devices.DigitalOutput;

public class FRCDigitalOutput implements DigitalOutput{

	private edu.wpi.first.wpilibj.DigitalOutput port;
	
	public FRCDigitalOutput(edu.wpi.first.wpilibj.DigitalOutput port) {
		this.port = port;
	}
	public FRCDigitalOutput(int port) {
		this(new edu.wpi.first.wpilibj.DigitalOutput(port));
	}
	
	@Override
	public void free() {
		if(port != null)
			port.free();
		port = null;
	}

	@Override
	public boolean get() {
		return port.get();
	}

	@Override
	public void set(boolean high) {
		port.set(high);
	}
	@Override
	public void pulse(double length) {
		port.pulse(length);
	}
}
