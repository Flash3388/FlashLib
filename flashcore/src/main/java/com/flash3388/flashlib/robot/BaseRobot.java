package com.flash3388.flashlib.robot;

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
