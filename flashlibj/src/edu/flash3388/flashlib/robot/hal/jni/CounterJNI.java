package edu.flash3388.flashlib.robot.hal.jni;

public final class CounterJNI {

	public static native int initializePulseCounter(int port);
	public static native int initializeQuadPulseCounter(int upPort, int downPort);
	
	public static native void freePulseCounter(int handle);
	
	public static native void resetPulseCounter(int handle);
	
	public static native boolean getPulseCounterDirection(int handle);
	public static native int getPulseCounterPulseCount(int handle);
	public static native float getPulseCounterPulseLength(int handle);
	public static native float getPulseCounterPulsePeriod(int handle);
	
	public static native boolean isPulseCounterQuadrature(int handle);
}
