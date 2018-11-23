package edu.flash3388.flashlib.robot;

import com.beans.BooleanProperty;
import com.beans.properties.SimpleBooleanProperty;
import edu.flash3388.flashlib.robot.modes.RobotMode;
import edu.flash3388.flashlib.time.Time;
import edu.flash3388.flashlib.util.concurrent.Sleeper;

/**
 * This class provides a simple extension of {@link RobotBase}, adding simple operation mode operation
 * which is executed by a robot loop.
 * <p>
 * The control loop tracks operation mode data and calls user methods accordingly. When in disabled
 * mode, {@link #disabled()} is called and allows user operations in disabled mode. When in any other mode,
 * {@link #onMode(RobotMode)} is called and the current mode value is passed, allowing user operations for that mode.
 * Those methods are called only once when in the operation mode. So if they finish execution before the mode is
 * finished, not further user code will be executed for that mode. If mode was changed and user code did not
 * finished and the methods did not return, this will disrupt robot operations.
 * <p>
 * Each iteration of the control loop puts the current thread into sleep.
 * <p>
 * {@link #robotInit()} is called when FlashLib finished initialization. Robot systems should be initialized here.
 * <p>
 * When the robot enters stop mode {@link #robotStop()} is called to allow user stop operations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public abstract class SimpleRobot extends RobotBase {

    private static final Time ITERATION_DELAY = Time.millis(5);

    private final Sleeper mSleeper;
    private final BooleanProperty mRunLoopProperty;

	protected SimpleRobot(Sleeper sleeper) {
		mSleeper = sleeper;
		mRunLoopProperty = new SimpleBooleanProperty(true);
	}

	@Override
	protected void robotMain() {
		robotLoop();
	}

	@Override
	protected void robotShutdown(){
		stopRobotLoop();

        robotStop();
	}

	protected void stopRobotLoop() {
	    mRunLoopProperty.setAsBoolean(false);
    }

	private void robotLoop(){
		while(mRunLoopProperty.getAsBoolean()){
		    RobotMode currentMode = getMode();

		    enterMode(currentMode);

            try {
                waitForModeToEnd(currentMode);
            } catch (InterruptedException e) {
                break;
            }
        }
	}

	private boolean stayInMode(RobotMode mode) {
		return isInMode(mode) && mRunLoopProperty.getAsBoolean();
	}

	private void enterMode(RobotMode mode) {
        if (mode.equals(RobotMode.DISABLED)) {
            disabled();
        } else {
            onMode(mode);
        }
    }

    private void waitForModeToEnd(RobotMode mode) throws InterruptedException {
        while(stayInMode(mode)){
            mSleeper.sleepWhileConditionMet(mRunLoopProperty, ITERATION_DELAY);
        }
    }

    //--------------------------------------------------------------------
    //----------------------Implementable---------------------------------
    //--------------------------------------------------------------------

	protected void robotStop(){}

	@Override
    protected abstract void robotInit() throws RobotInitializationException;

	protected abstract void disabled();
	protected abstract void onMode(RobotMode mode);
}
