package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;
import edu.flash3388.flashlib.util.FlashUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This class provides a simple extension of {@link RobotBase}, adding simple operation mode operation
 * which is executed by a robot loop.
 * <p>
 * The control loop tracks operation mode data and calls user methods accordingly. When in disabled
 * mode, {@link #disabled()} is called and allows user operations in disabled mode. When in any other mode,
 * {@link #onMode(int)} is called and the current mode value is passed, allowing user operations for that mode.
 * Those methods are called only once when in the operation mode. So if they finish execution before the mode is
 * finished, not further user code will be executed for that mode. If mode was changed and user code did not
 * finished and the methods did not return, this will disrupt robot operations.
 * <p>
 * Each iteration of the control loop puts the current thread into sleep for {@value #ITERATION_DELAY} milliseconds.
 * <p>
 * {@link #robotInit()} is called when FlashLib finished initialization. Robot systems should be initialized here.
 * <p>
 * If flashboard was initialized, {@link Flashboard#start()} is called automatically.
 * <p>
 * When the robot enters shutdown mode {@link #robotFree()} is called to allow user shutdown operations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public abstract class SimpleRobot extends RobotBase{
	
	public static final int ITERATION_DELAY = 5; //ms
	
	private AtomicBoolean mRunLoop;

	protected SimpleRobot() {
		mRunLoop = new AtomicBoolean(true);
	}

	@Override
	protected void robotMain() {
		robotLoop();
	}

	@Override
	protected void robotShutdown(){
		mRunLoop.compareAndSet(true, false);

		robotFree();
	}

	private void robotLoop(){
		while(mRunLoop.get()){
			if(isDisabled()){
				disabled();
				
				while(stayInMode(RobotModeSupplier.MODE_DISABLED)){
					FlashUtil.delay(ITERATION_DELAY);
				}
			} else{
				int currentMode = getMode();
				
				onMode(currentMode);
				
				while(stayInMode(currentMode)){
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
		}
	}

	private boolean stayInMode(int mode) {
		return isMode(mode) && mRunLoop.get();
	}

	protected void robotFree(){}
	protected abstract void disabled();
	protected abstract void onMode(int mode);
}
