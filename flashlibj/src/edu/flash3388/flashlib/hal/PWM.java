package edu.flash3388.flashlib.hal;

public class PWM extends HALPort{

	public PWM(int port) {
		handle = PWMJNI.initializePWMPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALException("Unable to initialize PWM: invalid HAL handle");
	}
	
	@Override
	public void free() {
		if(handle == HAL_INVALID_HANDLE){
			return;
		}
		
		setRaw(0);
		PWMJNI.freePWMPort(handle);
		handle = HAL_INVALID_HANDLE;
	}
	
	public int getRaw(){
		return PWMJNI.getPWMRaw(handle);
	}
	public float getDuty(){
		return PWMJNI.getPWMDuty(handle);
	}
	public float getFrequency(){
		return PWMJNI.getPWMFrequency(handle);
	}
	
	public void setRaw(int raw){
		PWMJNI.setPWMRaw(handle, raw);
	}
	public void setDuty(float duty){
		PWMJNI.setPWMDuty(handle, duty);
	}
	public void setFrequency(float frequency){
		PWMJNI.setPWMFrequency(handle, frequency);
	}
}
