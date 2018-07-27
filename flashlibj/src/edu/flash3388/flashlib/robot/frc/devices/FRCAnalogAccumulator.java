package edu.flash3388.flashlib.robot.frc.devices;

import edu.flash3388.flashlib.robot.devices.AnalogAccumulator;

public class FRCAnalogAccumulator implements AnalogAccumulator{

	private edu.wpi.first.wpilibj.AnalogInput port;
	
	FRCAnalogAccumulator(edu.wpi.first.wpilibj.AnalogInput port) {
		this.port = port;
	}
	
	@Override
	public void enable() {
		port.initAccumulator();
	}
	@Override
	public void disable() {
	}
	@Override
	public void reset() {
		port.resetAccumulator();
	}
	
	@Override
	public void setCenter(int value) {
		port.setAccumulatorCenter(value);
	}

	@Override
	public long getValue() {
		return port.getAccumulatorValue();
	}
	@Override
	public int getCount() {
		return (int) port.getAccumulatorCount();
	}
}
