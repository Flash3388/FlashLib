package edu.flash3388.flashlib.robot.hal.devices;

import edu.flash3388.flashlib.robot.hal.HALAnalogInput;
import edu.flash3388.flashlib.robot.io.devices.sensors.AnalogAccumulator;

public class HALAnalogAccumulator implements AnalogAccumulator{

	private HALAnalogInput inputPort;
	
	HALAnalogAccumulator(HALAnalogInput port) {
		this.inputPort = port;
	}
	
	@Override
	public void enable() {
		inputPort.enableAccumulator(true);
	}
	@Override
	public void disable() {
		inputPort.enableAccumulator(false);
	}
	@Override
	public void reset() {
		inputPort.resetAccumulator();
	}
	@Override
	public void setCenter(int value) {
		inputPort.setAccumulatorCenter(value);
	}
	@Override
	public long getValue() {
		return inputPort.getAccumulatorValue();
	}
	@Override
	public int getCount() {
		return inputPort.getAccumulatorCount();
	}
}
