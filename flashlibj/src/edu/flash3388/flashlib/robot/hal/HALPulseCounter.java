package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.devices.PulseCounter;

/**
 * Represents a pulse counter using FlashLib's Hardware Abstraction Layer. A pulse counter is used to
 * count pulses from digital input ports, and measure their length. When using
 * this, make sure HAL was initialized first. Before using this class, make sure the current HAL
 * implementation supports digital input ports.
 * <p>
 * This class implements {@link PulseCounter}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class HALPulseCounter extends HALPort implements PulseCounter{
	
	/**
	 * Creates a new pulse counter for the given DIO port using FlashLib's Hardware Abstraction Layer.
	 * If the counter initialization failed, for whatever reason, {@link HALException}
	 * is thrown.
	 * 
	 * @param port the HAL port of the desired digital input
	 * @throws HALException if counter initialization failed.
	 */
	public HALPulseCounter(int port) {
		handle = COUNTERJNI.initializePulseCounter(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALException("Unable to initialize PulseCounter: invalid HAL handle");
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
		
		COUNTERJNI.freePulseCounter(handle);
		handle = HAL_INVALID_HANDLE;
	}
	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the counter is reset.
	 */
	@Override 
	public void reset(){
		COUNTERJNI.resetPulseCounter(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current pulse count is returned.
	 */
	@Override 
	public int get(){
		return COUNTERJNI.getPulseCounterCount(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the period of the last pulse is returned.
	 */
	@Override
	public double getPeriod(){
		return (double)COUNTERJNI.getPulseCounterPeriod(handle);
	}
}
