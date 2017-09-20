package edu.flash3388.flashlib.robot.hal;

public final class DIOJNI {
	
	public static native int initializeDigitalInputPort(int port);
	public static native int initializeDigitalOutputPort(int port);
	
	public static native void freeDigitalInputPort(int handle);
	public static native void freeDigitalOutputPort(int handle);
	
	public static native boolean getDIO(int handle);
	public static native void setDIO(int handle, boolean high);
	
	public static native void pulseOutDIO(int handle, float length);
}
