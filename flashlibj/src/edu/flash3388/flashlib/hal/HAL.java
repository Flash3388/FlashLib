package edu.flash3388.flashlib.hal;

public final class HAL {

	public static final String NATIVE_LIBRARY_NAME = "flashlib_hal";
	
	public static void initializeHal(){
		System.loadLibrary(NATIVE_LIBRARY_NAME);
		initialize();
	}
	
	public static native int initialize();
	public static native int shutdown();
	
	public static native String boardName();
	
	public static native long getClockTime();
}
