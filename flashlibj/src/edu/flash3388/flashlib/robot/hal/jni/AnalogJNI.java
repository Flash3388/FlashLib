package edu.flash3388.flashlib.robot.hal.jni;

public final class AnalogJNI {

	public static native float getGlobalSampleRate();
	public static native float getMaxAnalogPortVoltage();
	public static native int getMaxAnalogPortValue();
	
	public static native float convertAnalogValueToVoltage(int value);
	public static native int convertAnalogVoltageToValue(float voltage);
	
	public static native boolean isAnalogInputPortValid(int port);
	public static native boolean isAnalogInputPortTaken(int port);

	public static native boolean isAnalogOutputPortValid(int port);
	public static native boolean isAnalogOutputPortTaken(int port);
	
	public static native int initializeAnalogInputPort(int port);
	public static native int initializeAnalogOutputPort(int port);
	
	public static native void freeAnalogInputPort(int handle);
	public static native void freeAnalogOutputPort(int handle);
	
	public static native int getAnalogValue(int handle);
	public static native float getAnalogVoltage(int handle);
	
	public static native void setAnalogVoltage(int handle, float voltage);
	public static native void setAnalogValue(int handle, int value);
}
