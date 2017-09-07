package edu.flash3388.flashlib.hal;

public final class ANALOGJNI {

	public static native int initializeAnalogInput(int port);
	public static native int initializeAnalogOutput(int port);
	
	public static native void freeAnalogInput(int handle);
	public static native void freeAnalogOutput(int handle);
	
	public static native int getAnalogValue(int handle);
	public static native float getAnalogVoltage(int handle);
	
	public static native void setAnalogVoltage(int handle, float voltage);
	public static native void setAnalogValue(int handle, int voltage);
}
