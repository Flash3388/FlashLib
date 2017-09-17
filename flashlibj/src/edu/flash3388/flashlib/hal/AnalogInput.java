package edu.flash3388.flashlib.hal;

public class AnalogInput extends HALPort{
	
	public AnalogInput(int port) {
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
	
	public int getValue(){
		return ANALOGJNI.getAnalogValue(handle);
	}
	public float getVoltage(){
		return ANALOGJNI.getAnalogVoltage(handle);
	}
}
