package edu.flash3388.flashlib.robot.hal.jni;

public class BoardJNI {

    /**
     * Gets the name of the current board, depending on the current implementation.
     * @return the board name.
     */
    public static native String getBoardName();
}
