package com.flash3388.flashlib.robot.motion;

import com.flash3388.flashlib.scheduling.Requirement;

/**
 * Represents a valve, which can be open or close.
 * 
 * @since FlashLib 3.1.0
 */
public interface Valve extends Requirement {

    /**
     * Open the valve
     */
    void open();

    /**
     * Close the valve
     */
    void close();

    /**
     * Gets whether the valve is open.
     * @return <b>true</b> if open, <b>false</b> if not
     */
    boolean isOpen();
}
