package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.PWM;

/**
 * Represents an PWM port using FlashLib's Hardware Abstraction Layer. When using
 * this, make sure HAL was initialized first. Before using this class, make sure the current HAL
 * implementation supports PWM ports.
 * <p>
 * This class implements {@link PWM}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class HALPWM extends HALPort implements PWM{

	/**
	 * Creates a new PWM port using FlashLib's Hardware Abstraction Layer.
	 * If the port initialization failed, for whatever reason, {@link HALException}
	 * is thrown.
	 * 
	 * @param port the HAL port of the desired PWM
	 * @throws HALException if port initialization failed.
	 */
	public HALPWM(int port) {
		handle = PWMJNI.initializePWMPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALException("Unable to initialize PWM: invalid HAL handle");
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was successfully initialized, the port is freed.
	 */
	@Override
	public void free() {
		if(handle == HAL_INVALID_HANDLE){
			return;
		}
		
		setRaw(0);
		PWMJNI.freePWMPort(handle);
		handle = HAL_INVALID_HANDLE;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current PWM output value
	 * is returned.
	 */
	@Override
	public int getRaw(){
		return PWMJNI.getPWMRaw(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current PWM output duty cycle
	 * is returned.
	 */
	@Override
	public double getDuty(){
		return (double)PWMJNI.getPWMDuty(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current PWM frequency
	 * is returned.
	 */
	@Override
	public double getFrequency(){
		return (double)PWMJNI.getPWMFrequency(handle);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current PWM output value
	 * is set.
	 */
	@Override
	public void setRaw(int raw){
		PWMJNI.setPWMRaw(handle, raw);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current PWM output duty cycle
	 * is set.
	 */
	@Override
	public void setDuty(double duty){
		PWMJNI.setPWMDuty(handle, (float)duty);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current PWM frequency
	 * is set.
	 */
	@Override
	public void setFrequency(double frequency){
		PWMJNI.setPWMFrequency(handle, (float)frequency);
	}
}
