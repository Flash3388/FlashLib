package edu.flash3388.flashlib.robot.hal;

import edu.flash3388.flashlib.robot.hal.HALIOProvider;
import edu.flash3388.flashlib.robot.io.IOProvider;

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
	
	/**
	 * Creates an {@link IOProvider} implementation using HAL.
	 * 
	 * @return a new {@link IOProvider} for HAL ports.
	 */
	public static IOProvider createIOProvider(){
		return new HALIOProvider();
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
	public static int initializeHAL(int mode){
		System.loadLibrary(NATIVE_LIBRARY_NAME);
		return initialize(mode);
	}
	
	/**
	 * Initializes FlashLib's HAL for a given mode. Insure that
	 * the native library was loaded first.
	 * <p>
	 * The init mode has diffrent impact depending on the HAL implementation.
	 * 
	 * @param mode the init mode. 
	 * @return 0 if initialization is successful
	 */
	public static native int initialize(int mode);
	/**
	 * Performs a shutdown to the HAL, freeing all ports and resources.
	 */
	public static native void shutdown();
	
	/**
	 * Gets the name of the current board, depending on the current implementation.
	 * @return the board name.
	 */
	public static native String boardName();
}
