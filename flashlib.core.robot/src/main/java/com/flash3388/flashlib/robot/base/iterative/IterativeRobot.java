package com.flash3388.flashlib.robot.base.iterative;

import com.flash3388.flashlib.robot.RobotControl;
import com.flash3388.flashlib.robot.RobotInitializationException;
import com.flash3388.flashlib.robot.modes.RobotMode;

/**
 * Defines a specialized robot base based on a looping and modes model.
 * Control is separated into control modes, as defined by {@link RobotControl#getMode()}.
 * Based on the mode, the <em>init</em> method for the mode is called once when entering from another
 * mode, and then <em>periodic</em> is called periodically, defined by the specific implementation
 * used.
 * <p>
 *     If {@link RobotMode#isDisabled()}, {@link #disabledInit()} and {@link #disabledPeriodic()} are called.
 *     Otherwise {@link #modeInit(RobotMode)} and {@link #modePeriodic(RobotMode)} are called with the exact mode.
 * </p>
 *
 * @since FlashLib 2.0.0
 *
 * @see LoopingRobotBase
 */
public interface IterativeRobot {

    /**
     * Initializer for creating {@link IterativeRobot} robot from {@link RobotControl}.
     * This is generally where the implementing {@link IterativeRobot} should
     * perform initializing to robot control and systems.
     *
     * @since FlashLib 2.0.0
     */
    @FunctionalInterface
    interface Initializer {
        IterativeRobot init(RobotControl robotControl) throws RobotInitializationException;
    }

    /**
     * Called when entering a mode with {@link RobotMode#isDisabled()} being <b>true</b>.
     */
    void disabledInit();

    /**
     * Called periodically while in a mode with {@link RobotMode#isDisabled()} being <b>true</b>.
     */
    void disabledPeriodic();

    /**
     * Called when entering a mode with {@link RobotMode#isDisabled()} being <b>false</b>.
     *
     * @param mode the current {@link RobotMode}.
     */
    void modeInit(RobotMode mode);

    /**
     * Called periodically while in a mode with {@link RobotMode#isDisabled()} being <b>false</b>.
     *
     * @param mode the current {@link RobotMode}.
     */
    void modePeriodic(RobotMode mode);

    /**
     * Called periodically after the mode-specific operations.
     */
    void robotPeriodic();

    /**
     * Called when the robot is stopping. Perform de-initialization here.
     */
    void robotStop();
}
