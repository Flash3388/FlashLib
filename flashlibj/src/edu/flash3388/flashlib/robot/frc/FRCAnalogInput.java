package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.devices.AnalogAccumulator;
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
	@Override
	public AnalogAccumulator getAccumulator() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public double getSampleRate() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public double getMaxVoltage() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int getMaxValue() {
		// TODO Auto-generated method stub
		return 0;
	}
}
