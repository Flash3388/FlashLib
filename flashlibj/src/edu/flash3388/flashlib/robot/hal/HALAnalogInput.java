package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.hal.jni.AnalogAccumulatorJNI;
import edu.flash3388.flashlib.robot.hal.jni.AnalogJNI;
import edu.flash3388.flashlib.robot.io.AnalogAccumulator;
import edu.flash3388.flashlib.robot.io.AnalogInput;

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
	
	private HALAnalogAccumulator accumulator;
	
	/**
	 * Creates a new analog input port using FlashLib's Hardware Abstraction Layer.
	 * If the port initialization failed, for whatever reason, {@link HALInitialzationException}
	 * is thrown.
	 * 
	 * @param port the HAL port of the desired analog input
	 * @throws HALInitialzationException if port initialization failed.
	 */
	public HALAnalogInput(int port) {
		if(!AnalogJNI.checkAnalogInputPortValid(port))
			throw new IllegalArgumentException("Invalid AnalogInput port "+port);
		
		if(AnalogJNI.checkAnalogInputPortTaken(port))
			throw new HALAllocationException("AnalogInput port taken", port);
		
		handle = AnalogJNI.initializeAnalogInputPort(port);
		if(handle == HAL_INVALID_HANDLE)
			throw new HALInitialzationException("Unable to initialize AnalogInput: invalid HAL handle", port);
		
		accumulator = new HALAnalogAccumulator(this);
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
		
		AnalogJNI.freeAnalogInputPort(handle);
		handle = HAL_INVALID_HANDLE;
		
		accumulator = null;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current analog value is returned.
	 */
	@Override 
	public int getValue(){
		return AnalogJNI.getAnalogValue(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * If the port was initialized, the current analog voltage is returned.
	 */
	@Override
	public double getVoltage(){
		return (double)AnalogJNI.getAnalogVoltage(handle);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Returns an accumulator wrapper object for this port. If the port was freed, 
	 * null is returned.
	 */
	@Override
	public AnalogAccumulator getAccumulator() {
		return accumulator;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This value is constant for all ports.
	 */
	@Override
	public double getSampleRate() {
		return AnalogJNI.getGlobalSampleRate();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This value is constant for all ports.
	 */
	@Override
	public double getMaxVoltage() {
		return AnalogJNI.getMaxAnalogPortVoltage();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This value is constant for all ports.
	 */
	@Override
	public int getMaxValue() {
		return AnalogJNI.getMaxAnalogPortValue();
	}
}
