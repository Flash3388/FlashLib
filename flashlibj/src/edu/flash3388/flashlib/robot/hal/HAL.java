package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.hal.jni.HALJNI;

/**
 * The main class for controlling FlashLib's Hardware Abstraction Layer.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public final class HAL {

	private HAL(){}
	
	/**
	 * The expected name of the HAL's native library.
	 */
	public static final String NATIVE_LIBRARY_NAME = "flashlib_hal";

	public static void loadNativeLibrary() {
		System.loadLibrary(NATIVE_LIBRARY_NAME);
	}
	
	/**
	 * Initialized the HAL for a given initialization mode. Loads the
	 * native library with the expected name from java.library.path by calling
	 * {@link System#loadLibrary(String)}. Then the HAL is initialized by calling 
	 * {@link #initialize(int)}.
	 * 
	 * @param mode the HAL init mode
	 * @return 0 if initialization is successful.
	 */
	public static int initialize(int mode){
		return HALJNI.initialize(mode);
	}

	public static void shutdown() {
		HALJNI.shutdown();
	}
}
