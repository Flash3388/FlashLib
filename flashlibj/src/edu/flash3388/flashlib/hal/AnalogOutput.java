package edu.flash3388.flashlib.hal;

public class AnalogOutput extends HALPort{
	
	public AnalogOutput(int port) {
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
	
	public int getValue(){
		return ANALOGJNI.getAnalogValue(handle);
	}
	public float getVoltage(){
		return ANALOGJNI.getAnalogVoltage(handle);
	}

	public void setValue(int value){
		ANALOGJNI.setAnalogValue(handle, value);
	}
	public void setVoltage(float voltage){
		ANALOGJNI.setAnalogVoltage(handle, voltage);
	}
}
