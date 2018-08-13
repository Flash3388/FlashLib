package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.hal.jni.AnalogAccumulatorJNI;
import edu.flash3388.flashlib.robot.io.AnalogAccumulator;

public class HALAnalogAccumulator implements AnalogAccumulator{

	private HALAnalogInput inputPort;
	
	HALAnalogAccumulator(HALAnalogInput port) {
		this.inputPort = port;
	}
	
	@Override
	public void enable() {
		setEnabled(true);
	}

	@Override
	public void disable() {
		setEnabled(false);
	}

	@Override
	public void reset() {
		AnalogAccumulatorJNI.resetAnalogInputAccumulator(inputPort.getHandle());
	}

	@Override
	public void setCenter(int value) {
		AnalogAccumulatorJNI.setAnalogInputAccumulatorCenter(inputPort.getHandle(), value);
	}

	@Override
	public long getValue() {
		return AnalogAccumulatorJNI.getAnalogInputAccumulatorValue(inputPort.getHandle());
	}

	@Override
	public int getCount() {
		return AnalogAccumulatorJNI.getAnalogInputAccumulatorCount(inputPort.getHandle());
	}


	public void setEnabled(boolean enable) {
		int result = AnalogAccumulatorJNI.enableAnalogInputAccumulator(inputPort.getHandle(), enable);

		if(result != 0){
			throw new HALInitializationException("Unable to "+(enable? "enable" : "disable")+
					" accumulator for analog input port", inputPort.getHandle());
		}
	}
}
