package edu.flash3388.flashlib.robot.hal.jni;

public final class DIOJNI {
	
	public static native boolean isDigitalInputPortValid(int port);
	public static native boolean isDigitalInputPortTaken(int port);
	
	public static native boolean isDigitalOutputPortValid(int port);
	public static native boolean isDigitalOutputPortTaken(int port);
	
	public static native int initializeDigitalInputPort(int port);
	public static native int initializeDigitalOutputPort(int port);
	
	public static native void freeDigitalInputPort(int handle);
	public static native void freeDigitalOutputPort(int handle);
	
	public static native boolean getDIO(int handle);
	public static native void setDIO(int handle, boolean high);
	
	public static native void pulseOutDIO(int handle, float length);
}
