package edu.flash3388.flashlib.hal;

public class DigitalInput extends HALPort{

	public DigitalInput(int port) {
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
	
	public boolean get(){
		return DIOJNI.getDIO(handle);
	}
}
