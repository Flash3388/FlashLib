package edu.flash3388.flashlib.robot.hal;

public final class ANALOGJNI {

	public static native float getGlobalSampleRate();
	public static native float getMaxAnalogPortVoltage();
	public static native int getMaxAnalogPortValue();
	
	public static native float convertAnalogValueToVoltage(int value);
	public static native int convertAnalogVoltageToValue(float voltage);
	
	
	public static native int initializeAnalogInputPort(int port);
	public static native int initializeAnalogOutputPort(int port);
	
	public static native void freeAnalogInputPort(int handle);
	public static native void freeAnalogOutputPort(int handle);
	
	public static native int getAnalogValue(int handle);
	public static native float getAnalogVoltage(int handle);
	
	public static native void setAnalogVoltage(int handle, float voltage);
	public static native void setAnalogValue(int handle, int value);
	
	public static native int enableAnalogInputAccumulator(int handle, boolean enable);
	public static native void resetAnalogInputAccumulator(int handle);
	public static native void setAnalogInputAccumulatorCenter(int handle, int center);
	public static native long getAnalogInputAccumulatorValue(int handle);
	public static native int getAnalogInputAccumulatorCount(int handle);
}
