package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.PWM;

public class HALPWM extends HALPort implements PWM{

	public HALPWM(int port) {
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
	
	@Override
	public int getRaw(){
		return PWMJNI.getPWMRaw(handle);
	}
	@Override
	public double getDuty(){
		return (double)PWMJNI.getPWMDuty(handle);
	}
	@Override
	public double getFrequency(){
		return (double)PWMJNI.getPWMFrequency(handle);
	}
	
	@Override
	public void setRaw(int raw){
		PWMJNI.setPWMRaw(handle, raw);
	}
	@Override
	public void setDuty(double duty){
		PWMJNI.setPWMDuty(handle, (float)duty);
	}
	@Override
	public void setFrequency(double frequency){
		PWMJNI.setPWMFrequency(handle, (float)frequency);
	}
}
