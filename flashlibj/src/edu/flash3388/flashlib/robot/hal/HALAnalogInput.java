package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.hal.jni.ANALOGJNI;
import edu.flash3388.flashlib.robot.io.devices.sensors.AnalogAccumulator;
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
		if(!ANALOGJNI.checkAnalogInputPortValid(port))
			throw new IllegalArgumentException("Invalid AnalogInput port "+port);
		
		if(ANALOGJNI.checkAnalogInputPortTaken(port))
			throw new HALAllocationException("AnalogInput port taken", port);
		
		handle = ANALOGJNI.initializeAnalogInputPort(port);
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
		
		ANALOGJNI.freeAnalogInputPort(handle);
		handle = HAL_INVALID_HANDLE;
		
		accumulator = null;
	}
	
	
	public void enableAccumulator(boolean enable){
		int result = ANALOGJNI.enableAnalogInputAccumulator(handle, enable);
		
		if(result != 0){
			throw new HALInitialzationException("Unable to "+(enable? "enable" : "disable")+
					" accumulator for analog input port", handle);
		}
	}
	public void resetAccumulator(){
		ANALOGJNI.resetAnalogInputAccumulator(handle);
	}
	public void setAccumulatorCenter(int value){
		ANALOGJNI.setAnalogInputAccumulatorCenter(handle, value);
	}
	public long getAccumulatorValue(){
		return ANALOGJNI.getAnalogInputAccumulatorValue(handle);
	}
	public int getAccumulatorCount(){
		return ANALOGJNI.getAnalogInputAccumulatorCount(handle);
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
		return ANALOGJNI.getGlobalSampleRate();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This value is constant for all ports.
	 */
	@Override
	public double getMaxVoltage() {
		return ANALOGJNI.getMaxAnalogPortVoltage();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This value is constant for all ports.
	 */
	@Override
	public int getMaxValue() {
		return ANALOGJNI.getMaxAnalogPortValue();
	}
}
