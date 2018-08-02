package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.hal.jni.CounterJNI;
import edu.flash3388.flashlib.robot.io.Counter;

import java.util.Collection;

/**
 * Represents a pulse counter using FlashLib's Hardware Abstraction Layer. A pulse counter is used to
 * count pulses from digital input ports, and measure their length. When using
 * this, make sure HAL was initialized first. Before using this class, make sure the current HAL
 * implementation supports digital input ports.
 * <p>
 * This class implements {@link Counter}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public class HALCounter extends HALResource implements Counter {

	/**
	 * Creates a new pulse counter for the given DIO port using FlashLib's Hardware Abstraction Layer.
	 * If the counter initialization failed, for whatever reason, {@link HALInitializationException}
	 * is thrown.
	 * 
	 * @param port the HAL port of the desired digital input
	 * @throws HALInitializationException if counter initialization failed.
	 */
	public HALCounter(HALDigitalInput port) {
		handle = CounterJNI.initializePulseCounter(port.getHandle());
		
		if(handle == HAL_INVALID_HANDLE) {
			throw new HALInitializationException("Unable to initialize Counter: invalid HAL handle",
					port.getHandle());
		}
	}

	/**
	 * Creates a new pulse counter for 2 given DIO ports using FlashLib's Hardware Abstraction Layer.
	 * If the counter initialization failed, for whatever reason, {@link HALInitializationException}
	 * is thrown. This initializes the counter to quadrature mode, counting pulses from 2 sources: 
	 * one forward, one backward.
	 * 
	 * @param upPort the HAL port of the desired forward digital input
	 * @param downPort the HAL port of the desired backward digital input
	 * @throws HALInitializationException if counter initialization failed.
	 */
	public HALCounter(HALDigitalInput upPort, HALDigitalInput downPort) {
		handle = CounterJNI.initializeQuadPulseCounter(upPort.getHandle(), downPort.getHandle());

		if(handle == HAL_INVALID_HANDLE) {
			throw new HALInitializationException("Unable to initialize Counter: invalid HAL handle",
					HAL_INVALID_HANDLE);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the counter was successfully initialized, the counter is freed.
	 */
	@Override
	public void free() {
		if(handle == HAL_INVALID_HANDLE){
			return;
		}
		
		CounterJNI.freePulseCounter(handle);
		handle = HAL_INVALID_HANDLE;
	}
	
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the counter is reset.
	 */
	@Override 
	public void reset(){
		CounterJNI.resetPulseCounter(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current pulse count is returned.
	 */
	@Override 
	public int get(){
		return CounterJNI.getPulseCounterPulseCount(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the length of the last pulse is returned.
	 */
	@Override
	public double getPulseLength(){
		return (double)CounterJNI.getPulseCounterPulseLength(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the period between last 2 pulses is returned.
	 */
	@Override
	public double getPulsePeriod() {
		return (double)CounterJNI.getPulseCounterPulsePeriod(handle);
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
		return CounterJNI.getPulseCounterDirection(handle);
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isQuadrature() {
		return CounterJNI.isPulseCounterQuadrature(handle);
	}
}
