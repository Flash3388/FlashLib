package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.devices.AnalogInput;

public class FRCAnalogInput implements AnalogInput{

	private edu.wpi.first.wpilibj.AnalogInput port;
	
	public FRCAnalogInput(edu.wpi.first.wpilibj.AnalogInput port) {
		this.port = port;
	}
	public FRCAnalogInput(int port) {
		this(new edu.wpi.first.wpilibj.AnalogInput(port));
	}
	
	@Override
	public void free() {
		if(port != null)
			port.free();
		port = null;
	}

	@Override
	public int getValue() {
		return port.getValue();
	}
	@Override
	public double getVoltage() {
		return port.getVoltage();
	}
}
