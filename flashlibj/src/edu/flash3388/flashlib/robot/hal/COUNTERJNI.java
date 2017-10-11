package edu.flash3388.flashlib.robot.hal;

public final class COUNTERJNI {

	public static native int initializePulseCounter(int port);
	public static native int initializeQuadPulseCounter(int upport, int downport);
	
	public static native void freePulseCounter(int handle);
	
	public static native void resetPulseCounter(int handle);
	
	public static native boolean getPulseCounterDirection(int handle);
	public static native int getPulseCounterPulseCount(int handle);
	public static native float getPulseCounterPulseLength(int handle);
	public static native float getPulseCounterPulsePeriod(int handle);
	
	public static native boolean isPulseCounterQuadrature(int handle);
}
