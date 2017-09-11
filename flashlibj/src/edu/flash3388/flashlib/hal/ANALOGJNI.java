package edu.flash3388.flashlib.hal;

public final class ANALOGJNI {

	public static native int initializeAnalogInputPort(int port);
	public static native int initializeAnalogOutputPort(int port);
	
	public static native void freeAnalogInputPort(int handle);
	public static native void freeAnalogOutputPort(int handle);
	
	public static native int getAnalogValue(int handle);
	public static native float getAnalogVoltage(int handle);
	
	public static native void setAnalogVoltage(int handle, float voltage);
	public static native void setAnalogValue(int handle, int value);
}
