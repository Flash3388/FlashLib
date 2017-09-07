package edu.flash3388.flashlib.hal;

public final class DIOJNI {
	
	public static native int initializeDigitalInput(int port);
	public static native int initializeDigitalOutput(int port);
	
	public static native void freeDigitalInput(int handle);
	public static native void freeDigitalOutput(int handle);
	
	public static native boolean get(int handle);
	public static native void set(int handle, boolean high);
	
	public static native void pulseOut(int handle, float length);
}
