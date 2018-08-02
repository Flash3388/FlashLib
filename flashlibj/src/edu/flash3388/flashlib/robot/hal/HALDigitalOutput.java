package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.hal.jni.DIOJNI;
import edu.flash3388.flashlib.robot.io.DigitalOutput;

/**
 * Represents an digital output port using FlashLib's Hardware Abstraction Layer. When using
 * this, make sure HAL was initialized first. Before using this class, make sure the current HAL
 * implementation supports digital output ports.
 * <p>
 * This class implements {@link DigitalOutput}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class HALDigitalOutput extends HALResource implements DigitalOutput{

	/**
	 * Creates a new digital output port using FlashLib's Hardware Abstraction Layer.
	 * If the port initialization failed, for whatever reason, {@link HALInitializationException}
	 * is thrown.
	 * 
	 * @param port the HAL port of the desired digital output
	 * @throws HALInitializationException if port initialization failed.
	 */
	public HALDigitalOutput(int port) {
		if(!DIOJNI.checkDigitalOutputPortValid(port))
			throw new IllegalArgumentException("Invalid DigitalOutput port "+port);
		
		if(DIOJNI.checkDigitalOutputPortTaken(port))
			throw new HALAllocationException("DigitalOutput port taken", port);
		
		handle = DIOJNI.initializeDigitalOutputPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALInitializationException("Unable to initialize DigitalOutput: invalid HAL handle", port);
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
		
		set(false);
		DIOJNI.freeDigitalOutputPort(handle);
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
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current digital value is set
	 */
	@Override
	public void set(boolean high){
		DIOJNI.setDIO(handle, high);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current digital value is set
	 * to high for the given amount of time
	 */
	@Override
	public void pulse(double length){
		DIOJNI.pulseOutDIO(handle, (float)length);
	}
}
