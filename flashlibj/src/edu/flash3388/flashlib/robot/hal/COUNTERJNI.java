package edu.flash3388.flashlib.robot.hal;

public final class COUNTERJNI {

	public static native int initializePulseCounter(int port);
	
	public static native void freePulseCounter(int handle);
	
	public static native void resetPulseCounter(int handle);
	
	public static native int getPulseCounterPulseCount(int handle);
	public static native float getPulseCounterPulseLength(int handle);
	public static native float getPulseCounterPulsePeriod(int handle);
}
