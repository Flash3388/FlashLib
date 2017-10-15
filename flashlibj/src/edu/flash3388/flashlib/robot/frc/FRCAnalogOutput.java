package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.devices.AnalogOutput;

public class FRCAnalogOutput implements AnalogOutput{

	private edu.wpi.first.wpilibj.AnalogOutput port;
	
	public FRCAnalogOutput(edu.wpi.first.wpilibj.AnalogOutput port) {
		this.port = port;
	}
	public FRCAnalogOutput(int port) {
		this(new edu.wpi.first.wpilibj.AnalogOutput(port));
	}
	
	@Override
	public void free() {
		if(port != null)
			port.free();
		port = null;
	}

	@Override
	public void setValue(int value) {
		port.setVoltage(value / 4096.0 * 5.0);
	}
	@Override
	public void setVoltage(double voltage) {
		port.setVoltage(voltage);
	}

	@Override
	public int getValue() {
		return (int) (port.getVoltage() / 5.0 * 4096);
	}
	@Override
	public double getVoltage() {
		return port.getVoltage();
	}
}
