package edu.flash3388.flashlib.hal;

import edu.flash3388.flashlib.robot.devices.DigitalInput;

public class HALDigitalInput extends HALPort implements DigitalInput{

	public HALDigitalInput(int port) {
		handle = DIOJNI.initializeDigitalInputPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALException("Unable to initialize DigitalInput: invalid HAL handle");
	}
	
	@Override
	public void free() {
		if(handle == HAL_INVALID_HANDLE){
			return;
		}
		
		DIOJNI.freeDigitalInputPort(handle);
		handle = HAL_INVALID_HANDLE;
	}
	
	@Override
	public boolean get(){
		return DIOJNI.getDIO(handle);
	}
}
