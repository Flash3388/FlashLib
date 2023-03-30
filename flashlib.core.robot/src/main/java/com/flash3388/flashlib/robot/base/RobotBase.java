package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.app.StartupException;
import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.base.iterative.LoopingRobotBase;

/**
 * Base interface for defining robot behaviour. User robot
 * code should implement this, or an extension related to it.
 *
 * @since FlashLib 3.0.0
 *
 * @see LoopingRobotBase
 */
public interface RobotBase {

    /**
     * Called when robot initialization starts, allowing for initialization of user code.
     *
     * @param robotControl object for accessing and controlling robot resources and components.
     *
     * @throws StartupException if an error occurs while initializing
     */
    void robotInit(RobotControl robotControl) throws StartupException;

    /**
     * Called when {@link RobotControl} finished initialization and the robot can be started.
     * This is the main method of the robot and all operations should be directed from here.
     */
    void robotMain();

    /**
     * Called when the robot finishes running, allowing to perform custom stop operations. Should be used
     * to free robot components.
     */
    void robotShutdown();
}
