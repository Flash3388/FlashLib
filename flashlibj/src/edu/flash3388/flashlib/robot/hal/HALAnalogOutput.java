package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.io.AnalogOutput;

/**
 * Represents an analog output port using FlashLib's Hardware Abstraction Layer. When using
 * this, make sure HAL was initialized first. Before using this class, make sure the current HAL
 * implementation supports analog output ports.
 * <p>
 * This class implements {@link AnalogOutput}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class HALAnalogOutput extends HALPort implements AnalogOutput{
	
	/**
	 * Creates a new analog output port using FlashLib's Hardware Abstraction Layer.
	 * If the port initialization failed, for whatever reason, {@link HALInitialzationException}
	 * is thrown.
	 * 
	 * @param port the HAL port of the desired analog output
	 * @throws HALInitialzationException if port initialization failed.
	 */
	public HALAnalogOutput(int port) {
		if(!ANALOGJNI.checkAnalogOutputPortValid(port))
			throw new IllegalArgumentException("Invalid AnalogOutput port "+port);
		
		if(ANALOGJNI.checkAnalogOutputPortTaken(port))
			throw new HALAllocationException("AnalogOutput port taken", port);
		
		handle = ANALOGJNI.initializeAnalogOutputPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALInitialzationException("Unable to initialize AnalogOutput: invalid HAL handle", port);
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
		
		setValue(0);
		ANALOGJNI.freeAnalogOutputPort(handle);
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

	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the output analog value is set.
	 */
	@Override
	public void setValue(int value){
		ANALOGJNI.setAnalogValue(handle, value);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the output voltage is set.
	 */
	@Override
	public void setVoltage(double voltage){
		ANALOGJNI.setAnalogVoltage(handle, (float)voltage);
	}
}
