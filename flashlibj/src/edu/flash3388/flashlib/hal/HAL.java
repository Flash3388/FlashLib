package edu.flash3388.flashlib.hal;

public final class HAL {

	public static final String NATIVE_LIBRARY_NAME = "flashlib_hal";
	
	public static void initializeHAL(int mode){
		System.loadLibrary(NATIVE_LIBRARY_NAME);
		initialize(mode);
	}
	
	public static native int initialize(int mode);
	public static native void shutdown();
	
	public static native String boardName();
}
