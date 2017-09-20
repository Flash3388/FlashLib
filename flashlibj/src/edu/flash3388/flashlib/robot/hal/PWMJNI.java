package edu.flash3388.flashlib.robot.hal;

public final class PWMJNI {
	
	public static native int initializePWMPort(int port);
	
	public static native void freePWMPort(int handle);
	
	public static native void setPWMRaw(int handle, int raw);
	public static native void setPWMDuty(int handle, float duty);
	public static native void setPWMFrequency(int handle, float frequency);
	
	public static native int getPWMRaw(int handle);
	public static native float getPWMDuty(int handle);
	public static native float getPWMFrequency(int handle);
}
