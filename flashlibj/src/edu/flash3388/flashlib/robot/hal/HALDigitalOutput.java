package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.DigitalOutput;

public class HALDigitalOutput extends HALPort implements DigitalOutput{

	public HALDigitalOutput(int port) {
		handle = DIOJNI.initializeDigitalOutputPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALException("Unable to initialize DigitalOutput: invalid HAL handle");
	}
	
	@Override
	public void free() {
		if(handle == HAL_INVALID_HANDLE){
			return;
		}
		
		set(false);
		DIOJNI.freeDigitalOutputPort(handle);
		handle = HAL_INVALID_HANDLE;
	}
	
	@Override
	public boolean get(){
		return DIOJNI.getDIO(handle);
	}
	
	@Override
	public void set(boolean high){
		DIOJNI.setDIO(handle, high);
	}
	@Override
	public void pulse(double length){
		DIOJNI.pulseOutDIO(handle, (float)length);
	}
}
