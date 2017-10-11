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
	 * Creates a new pulse counter for 2 given DIO ports using FlashLib's Hardware Abstraction Layer.
	 * If the counter initialization failed, for whatever reason, {@link HALException}
	 * is thrown. This initializes the counter to quadrature mode, counting pulses from 2 sources: 
	 * one forward, one backward.
	 * 
	 * @param upPort the HAL port of the desired forward digital input
	 * @param downPort the HAL port of the desired backward digital input
	 * @throws HALException if counter initialization failed.
	 */
	public HALPulseCounter(int upPort, int downPort) {
		handle = COUNTERJNI.initializeQuadPulseCounter(upPort, downPort);
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
		return COUNTERJNI.getPulseCounterPulseCount(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the length of the last pulse is returned.
	 */
	@Override
	public double getPulseLength(){
		return (double)COUNTERJNI.getPulseCounterPulseLength(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the period between last 2 pulses is returned.
	 */
	@Override
	public double getPulsePeriod() {
		return (double)COUNTERJNI.getPulseCounterPulsePeriod(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the rotation direction is returned.
	 */
	@Override
	public boolean getDirection() {
		if(!isQuadrature())
			return true;
		return COUNTERJNI.getPulseCounterDirection(handle);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isQuadrature() {
		return COUNTERJNI.isPulseCounterQuadrature(handle);
	}
}
