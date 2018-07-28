package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.modes.RobotMode;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;
import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Scheduler;
import edu.flash3388.flashlib.robot.scheduling.SchedulerRunMode;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.io.devices.actuators.MotorSafetyHelper;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An extension of {@link RobotBase}. This class provides extended and easier control over robot
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
 * Each iteration of the control loop puts the current thread into sleep for {@value #ITERATION_DELAY} milliseconds.
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
 * addition, when in disabled mode the scheduling enters {@link Scheduler#MODE_TASKS} mode so {@link Action} objects
 * are not executed, only tasks are, this is for safety of operation.
 * <p>
 * The {@link MotorSafetyHelper} class is used to provide safety for motor operations. When in disabled mode,
 * {@link MotorSafetyHelper#disableAll()} is used to disable usage of safe actuators. When in other operation
 * modes, {@link MotorSafetyHelper#checkAll()} is called periodically (every {@value #SAFETY_CHECK_COUNTER} iterations) 
 * to insure motor safety.
 * <p>
 * If flashboard was initialized, {@link Flashboard#start()} is called automatically.
 * <p>
 * When the robot enters shutdown mode {@link #robotFree()} is called to allow user shutdown operations.
 * 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.2
 */
public abstract class IterativeRobot extends RobotBase {
	
	public static final int ITERATION_DELAY = 5; //ms
	
	private AtomicBoolean mRunLoop;
	private Scheduler mScheduler;

	protected IterativeRobot() {
	    mRunLoop = new AtomicBoolean(true);
	    mScheduler = Scheduler.getInstance();
    }

	@Override
	protected void robotMain() {
		robotLoop();
	}

	@Override
	protected void robotShutdown(){
        mRunLoop.compareAndSet(true, false);

		mScheduler.removeAllActions();
		mScheduler.setRunMode(SchedulerRunMode.DISABLED);

		robotFree();
	}

    private void robotLoop(){
        while(mRunLoop.get()){
            if(isDisabled()){
                mScheduler.removeAllActions();
                mScheduler.setRunMode(SchedulerRunMode.TASKS_ONLY);

                disabledInit();

                while(stayInMode(RobotMode.DISABLED)){
                    mScheduler.run();

                    disabledPeriodic();

                    FlashUtil.delay(ITERATION_DELAY);
                }
            } else{
                RobotMode currentMode = getMode();

                mScheduler.removeAllActions();
                mScheduler.setRunMode(SchedulerRunMode.ALL);

                modeInit(currentMode);

                while(stayInMode(currentMode)){
                    mScheduler.run();

                    modePeriodic(currentMode);

                    FlashUtil.delay(ITERATION_DELAY);
                }
            }
        }
    }

    private boolean stayInMode(RobotMode mode) {
	    return isInMode(mode) && mRunLoop.get();
    }

	protected void robotFree(){}
	
	protected abstract void disabledInit();
	protected abstract void disabledPeriodic();
	
	protected abstract void modeInit(RobotMode mode);
	protected abstract void modePeriodic(RobotMode mode);
}
