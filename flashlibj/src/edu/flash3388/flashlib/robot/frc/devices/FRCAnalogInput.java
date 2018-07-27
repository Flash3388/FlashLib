package edu.flash3388.flashlib.robot.frc.devices;

import edu.flash3388.flashlib.robot.devices.AnalogAccumulator;
import edu.flash3388.flashlib.robot.devices.AnalogInput;

public class FRCAnalogInput implements AnalogInput{

	private edu.wpi.first.wpilibj.AnalogInput port;
	private FRCAnalogAccumulator accumulator;
	
	public FRCAnalogInput(edu.wpi.first.wpilibj.AnalogInput port) {
		this.port = port;
		
		accumulator = new FRCAnalogAccumulator(port);
	}
	public FRCAnalogInput(int port) {
		this(new edu.wpi.first.wpilibj.AnalogInput(port));
	}
	
	@Override
	public void free() {
		if(port != null)
			port.free();
		port = null;
		accumulator = null;
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
		return accumulator;
	}
	
	@Override
	public double getSampleRate() {
		return edu.wpi.first.wpilibj.AnalogInput.getGlobalSampleRate();
	}
	@Override
	public double getMaxVoltage() {
		return 5.0;
	}
	@Override
	public int getMaxValue() {
		return 4096;
	}
}
