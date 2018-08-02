package edu.flash3388.flashlib.robot.hal.jni;

public class HALJNI {

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
