package edu.flash3388.flashlib.robot;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import edu.flash3388.flashlib.robot.hid.HidInterface;
import edu.flash3388.flashlib.robot.modes.RobotMode;
import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.robot.scheduling.SchedulerRunMode;
import edu.flash3388.flashlib.time.Clock;
import edu.flash3388.flashlib.time.Time;
import edu.flash3388.flashlib.util.concurrent.Sleeper;
import edu.flash3388.flashlib.util.resources.ResourceHolder;

import java.util.logging.Logger;

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
 * is in a mode, the {@link Scheduler#run()} method is executed periodically, insuring correct operation
 * of that system. When operation modes change, all {@link Action} objects are interrupted by calling
 * {@link Scheduler#removeAllActions()} so that unwanted execution will not remain and cause issues. In
 * addition, when in disabled mode the scheduling enters {@link SchedulerRunMode#TASKS_ONLY} mode so {@link Action} objects
 * are not executed, only tasks are, this is for safety of operation.
 * <p>
 * When the robot enters stop mode {@link #robotStop()} is called to allow user stop operations.
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public abstract class IterativeRobot extends RobotBase {

	private static final Time ITERATION_DELAY = Time.milliseconds(5);

	private final Sleeper mSleeper;
	private final BooleanProperty mRunLoopProperty;

	protected IterativeRobot(Clock clock, Scheduler scheduler, HidInterface hidInterface, Logger logger, ResourceHolder resourceHolder, Sleeper sleeper) {
        super(clock, scheduler, hidInterface, logger, resourceHolder);

	    mSleeper = sleeper;
        mRunLoopProperty = new SimpleBooleanProperty(true);
    }

    protected IterativeRobot(Clock clock, Scheduler scheduler, HidInterface hidInterface, Logger logger, ResourceHolder resourceHolder) {
        this(clock, scheduler, hidInterface, logger, resourceHolder, new Sleeper());
    }

	@Override
	protected void robotMain() {
		robotLoop();
	}

	@Override
	protected void robotShutdown(){
        stopRobotLoop();

        mScheduler.setRunMode(SchedulerRunMode.DISABLED);
        mScheduler.removeAllTasks();
        mScheduler.removeAllActions();

        robotStop();
	}

	public Scheduler getScheduler() {
	    return mScheduler;
    }

	protected void stopRobotLoop() {
        mRunLoopProperty.setAsBoolean(false);
    }

    private void robotLoop(){
	    RobotMode currentMode;
	    RobotMode lastMode = null;

	    boolean wasModeInitialize = false;

        while(mRunLoopProperty.getAsBoolean()){
            currentMode = getMode();

            if (!currentMode.equals(lastMode)) {
                lastMode = currentMode;
                wasModeInitialize = false;
            }

            if (!wasModeInitialize) {
                initMode(currentMode);
                wasModeInitialize = true;
            }

            periodicMode(currentMode);

            try {
                mSleeper.sleepWhileConditionMet(mRunLoopProperty, ITERATION_DELAY);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    private void initMode(RobotMode mode) {
        mScheduler.removeAllActions();

        if (mode.equals(RobotMode.DISABLED)) {
            mScheduler.setRunMode(SchedulerRunMode.TASKS_ONLY);
            disabledInit();
        } else {
            mScheduler.setRunMode(SchedulerRunMode.ALL);
            modeInit(mode);
        }
    }

    private void periodicMode(RobotMode mode) {
        if (mode.equals(RobotMode.DISABLED)) {
            disabledPeriodic();
        } else {
            mScheduler.run();
            modePeriodic(mode);
        }

        robotPeriodic();
    }

    //--------------------------------------------------------------------
    //----------------------Implementable---------------------------------
    //--------------------------------------------------------------------

	protected void robotStop(){}

	@Override
	protected abstract void robotInit() throws RobotInitializationException;
    protected abstract void robotPeriodic();

	protected abstract void disabledInit();
	protected abstract void disabledPeriodic();
	
	protected abstract void modeInit(RobotMode mode);
	protected abstract void modePeriodic(RobotMode mode);
}
