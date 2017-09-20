package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.AnalogInput;

public class HALAnalogInput extends HALPort implements AnalogInput{
	
	public HALAnalogInput(int port) {
		handle = ANALOGJNI.initializeAnalogInputPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALException("Unable to initialize AnalogInput: invalid HAL handle");
	}
	
	@Override
	public void free() {
		if(handle == HAL_INVALID_HANDLE){
			return;
		}
		
		ANALOGJNI.freeAnalogInputPort(handle);
		handle = HAL_INVALID_HANDLE;
	}
	
	@Override
	public int getValue(){
		return ANALOGJNI.getAnalogValue(handle);
	}
	@Override
	public double getVoltage(){
		return (double)ANALOGJNI.getAnalogVoltage(handle);
	}
}
