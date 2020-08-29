package com.flash3388.flashlib.robot.base;

import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;

/**
 * Base interface for defining robot behaviour. User robot
 * code should implement this, or an extension related to it.
 *
 * @since FlashLib 3.0.0
 *
 * @see com.flash3388.flashlib.robot.base.iterative.LoopingRobotControl
 */
public interface BaseRobot extends RobotControl {

    /**
     * Called when robot initialization starts, allowing for initialization of user code.
     *
     * @throws RobotInitializationException if an error occurs while initializing
     */
    void robotInit() throws RobotInitializationException;

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
