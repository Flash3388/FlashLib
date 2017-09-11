package edu.flash3388.flashlib.hal;

public final class PWMJNI {
	
	public static native int initializePWMPort(int port);
	
	public static native void freePWMPort(int handle);
	
	public static native void setPWMRaw(int handle, int raw);
	public static native void setPWMDuty(int handle, float duty);
	
	public static native int getPWMRaw(int handle);
	public static native float getPWMDuty(int handle);
}
