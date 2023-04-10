package com.flash3388.flashlib.robot.systems;

import com.flash3388.flashlib.scheduling.Requirement;
import com.flash3388.flashlib.scheduling.actions.Action;

public interface Piston extends Requirement {

    /**
     * Opens the piston.
     *
     * @return action to perform motion.
     */
    Action open();

    /**
     * Closes the piston.
     *
     * @return action to perform motion.
     */
    Action close();

    /**
     * Toggles the piston state: if open, closes it; if closed, opens it.
     *
     * @return action to perform motion.
     */
    Action toggle();

    /**
     * Gets whether the piston is open or close.
     *
     * @return <b>true</b> if open, <b>false</b> if closed.
     */
    boolean isOpen();
}
