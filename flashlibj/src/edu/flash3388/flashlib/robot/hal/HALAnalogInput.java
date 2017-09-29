package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.AnalogInput;

/**
 * Represents an analog input port using FlashLib's Hardware Abstraction Layer. When using
 * this, make sure HAL was initialized first. Before using this class, make sure the current HAL
 * implementation supports analog input ports.
 * <p>
 * This class implements {@link AnalogInput}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class HALAnalogInput extends HALPort implements AnalogInput{
	
	/**
	 * Creates a new analog input port using FlashLib's Hardware Abstraction Layer.
	 * If the port initialization failed, for whatever reason, {@link HALException}
	 * is thrown.
	 * 
	 * @param port the HAL port of the desired analog input
	 * @throws HALException if port initialization failed.
	 */
	public HALAnalogInput(int port) {
		handle = ANALOGJNI.initializeAnalogInputPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALException("Unable to initialize AnalogInput: invalid HAL handle");
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
		
		ANALOGJNI.freeAnalogInputPort(handle);
		handle = HAL_INVALID_HANDLE;
	}
	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current analog value is returned.
	 */
	@Override 
	public int getValue(){
		return ANALOGJNI.getAnalogValue(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current analog voltage is returned.
	 */
	@Override
	public double getVoltage(){
		return (double)ANALOGJNI.getAnalogVoltage(handle);
	}
}
