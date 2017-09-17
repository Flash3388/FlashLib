package edu.flash3388.flashlib.hal;

public class DigitalOutput extends HALPort{

	public DigitalOutput(int port) {
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
	
	public boolean get(){
		return DIOJNI.getDIO(handle);
	}
	
	public void set(boolean high){
		DIOJNI.setDIO(handle, high);
	}
	public void pulse(float length){
		DIOJNI.pulseOutDIO(handle, length);
	}
}
