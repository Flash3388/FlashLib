package edu.flash3388.flashlib.hal;

public final class PWMJNI {
	
	public static native int initializePWM(int port);
	
	public static native void freePWM(int handle);
	
	public static native void setRaw(int handle, int raw);
	public static native void setDuty(int handle, float duty);
	
	public static native int getRaw(int handle);
	public static native float getDuty(int handle);
}
