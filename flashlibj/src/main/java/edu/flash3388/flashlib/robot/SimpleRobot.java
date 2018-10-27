package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.modes.RobotMode;

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
 * Each iteration of the control loop puts the current thread into sleep for {@value #ITERATION_DELAY_MS} milliseconds.
 * <p>
 * {@link #robotInit()} is called when FlashLib finished initialization. Robot systems should be initialized here.
 * <p>
 * When the robot enters stop mode {@link #robotStop()} is called to allow user stop operations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public abstract class SimpleRobot extends RobotBase {
	
	private static final long ITERATION_DELAY_MS = 5;
	
	private boolean mRunLoop;

	protected SimpleRobot() {
		mRunLoop = true;
	}

	@Override
	protected void robotMain() {
		robotLoop();
	}

	@Override
	protected void robotShutdown(){
		mRunLoop = false;

        robotStop();
	}

	protected void stopRobotLoop() {
	    mRunLoop = false;
    }

	private void robotLoop(){
		while(mRunLoop){
			if(isDisabled()){
				disabled();
				
				while(stayInMode(RobotMode.DISABLED)){
                    try {
                        Thread.sleep(ITERATION_DELAY_MS);
                    } catch (InterruptedException e) {
                        break;
                    }
				}
			} else{
				RobotMode currentMode = getMode();
				
				onMode(currentMode);
				
				while(stayInMode(currentMode)){
                    try {
                        Thread.sleep(ITERATION_DELAY_MS);
                    } catch (InterruptedException e) {
                        break;
                    }
				}
			}
		}
	}

	private boolean stayInMode(RobotMode mode) {
		return isInMode(mode) && mRunLoop;
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
