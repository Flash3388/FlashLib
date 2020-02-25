package com.flash3388.flashlib.robot;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import com.flash3388.flashlib.robot.modes.RobotMode;
import com.flash3388.flashlib.robot.scheduling.Scheduler;
import com.flash3388.flashlib.time.Clock;
import com.flash3388.flashlib.time.Time;
import com.flash3388.flashlib.util.concurrent.Sleeper;

/**
 * An extension of {@link Robot}. This class provides extended and easier control over robot
 * operation by providing a control loop which calls user methods depending on the operation mode.
 * <p>
 * The control loop divides each operation mode into two types
 * <ul>
 * 	<li> init: initialization of the operation mode</li>
 *  <li> periodic: execution of the operation mode</li>
 * </ul>
 * `init` is called every time the robot enters a new mode. `periodic` is called every ~10ms while the robot
 * is in the operation mode.
 * <p>
 * Users extending this class must implement:
 * <ul>
 * 	<li> {@link #robotInit()}: initialization of robot systems
 * 	<li> {@link #disabledInit()}: initialization for disabled mode </li>
 * 	<li> {@link #disabledPeriodic()}: execution of disabled mode </li>
 *  <li> {@link #modeInit(RobotMode)}: initialization for a given operation mode </li>
 * 	<li> {@link #modePeriodic(RobotMode)}: execution of a given operation mode </li>
 * </ul>
 * {@link #robotInit()} is called after FlashLib systems finished initialization and are ready to be used.
 * Use this to initialize robot systems.
 * <p>
 * Since FlashLib supports custom operation modes, {@link #modeInit(RobotMode)} and {@link #modePeriodic(RobotMode)} are
 * called for every operation mode that is not disabled mode. The passed parameter is the mode's value, so it
 * is recommended to pay attention to the value.
 * <p>
 * Each iteration of the control loop puts the current thread into sleep.
 * <p>
 * The control loop which is provided here performs several background operations before execution of user 
 * code:
 * <ul>
 * 	<li> execution of FlashLib's scheduling system </li>
 * 	<li> execution of FlashLib's motor safety </li>
 * </ul>
 * The scheduling system is updated by the control loop to allow operation of that system. While the robot
 * is in a mode, the {@link Scheduler#run(RobotMode)} method is executed periodically, insuring correct operation
 * of that system.
 * <p>
 * When the robot enters stop mode {@link #robotStop()} is called to allow user stop operations.
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public abstract class IterativeRobot extends IterativeRobotBase {

	private static final Time ITERATION_DELAY = Time.milliseconds(5);

	private final Sleeper mSleeper;
	private final BooleanProperty mRunLoopProperty;

	protected IterativeRobot(Sleeper sleeper) {
        mSleeper = sleeper;
        mRunLoopProperty = new SimpleBooleanProperty(true);
    }

    protected IterativeRobot() {
        this(new Sleeper());
    }

	@Override
	protected final void robotMain() {
        while(mRunLoopProperty.getAsBoolean()){
            robotLoop();

            try {
                mSleeper.sleepWhileConditionMet(mRunLoopProperty, ITERATION_DELAY);
            } catch (InterruptedException e) {
                break;
            }
        }
	}

	@Override
	protected final void stopRobotLoop() {
        mRunLoopProperty.setAsBoolean(false);
    }
}
