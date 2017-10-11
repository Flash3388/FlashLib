package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.DigitalInput;

/**
 * Represents an digital input port using FlashLib's Hardware Abstraction Layer. When using
 * this, make sure HAL was initialized first. Before using this class, make sure the current HAL
 * implementation supports digital input ports.
 * <p>
 * This class implements {@link DigitalInput}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class HALDigitalInput extends HALPort implements DigitalInput{

	/**
	 * Creates a new digital input port using FlashLib's Hardware Abstraction Layer.
	 * If the port initialization failed, for whatever reason, {@link HALException}
	 * is thrown.
	 * 
	 * @param port the HAL port of the desired digital input
	 * @throws HALException if port initialization failed.
	 */
	public HALDigitalInput(int port) {
		if(!DIOJNI.checkDigitalInputPortValid(port))
			throw new IllegalArgumentException("Invalid DigitalInput port "+port);
		
		if(DIOJNI.checkDigitalInputPortTaken(port))
			throw new HALAllocationException("DigitalInput port taken", port);
		
		handle = DIOJNI.initializeDigitalInputPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALException("Unable to initialize DigitalInput: invalid HAL handle", port);
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
		
		DIOJNI.freeDigitalInputPort(handle);
		handle = HAL_INVALID_HANDLE;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current digital value is returned.
	 */
	@Override
	public boolean get(){
		return DIOJNI.getDIO(handle);
	}
}
