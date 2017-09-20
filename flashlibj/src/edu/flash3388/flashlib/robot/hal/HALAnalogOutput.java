package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.AnalogOutput;

public class HALAnalogOutput extends HALPort implements AnalogOutput{
	
	public HALAnalogOutput(int port) {
		handle = ANALOGJNI.initializeAnalogOutputPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALException("Unable to initialize AnalogOutput: invalid HAL handle");
	}
	
	@Override
	public void free() {
		if(handle == HAL_INVALID_HANDLE){
			return;
		}
		
		setValue(0);
		ANALOGJNI.freeAnalogOutputPort(handle);
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

	public void setValue(int value){
		ANALOGJNI.setAnalogValue(handle, value);
	}
	public void setVoltage(double voltage){
		ANALOGJNI.setAnalogVoltage(handle, (float)voltage);
	}
}
