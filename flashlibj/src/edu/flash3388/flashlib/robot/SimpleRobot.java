package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.util.FlashUtil;

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
 * This class provides extended custom initialization. When the robot is initializing, {@link #preInit(SimpleRobotInitializer)}
 * is called for custom initialization. The passed object, {@link SimpleRobotInitializer} is an extension
 * of {@link RobotInitializer} which adds additional initialization options.
 * <p>
 * If flashboard was initialized, {@link Flashboard#start()} is called automatically.
 * <p>
 * When the robot enters shutdown mode {@link #robotShutdown()} is called to allow user shutdown operations.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public abstract class SimpleRobot extends RobotBase{
	
	protected static class SimpleRobotInitializer extends RobotInitializer{
		
		public void copy(SimpleRobotInitializer initializer){
			super.copy(initializer);
		}
	}
	
	public static final int ITERATION_DELAY = 5; //ms
	
	private boolean stop = false;
	
	private void robotLoop(){
		if((Flashboard.getInitMode() & Flashboard.INIT_COMM) != 0)
			Flashboard.start();
		
		int lastState;
		while(!stop){
			if(FlashRobotUtil.inEmergencyStop()){
				
				while(FlashRobotUtil.inEmergencyStop() && !stop){
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
			else if(isDisabled()){
				disabled();
				
				while(isDisabled() && !FlashRobotUtil.inEmergencyStop() && !stop){
					FlashUtil.delay(ITERATION_DELAY);
				}
			}else{
				lastState = getMode();
				
				onMode(lastState);
				
				while(isMode(lastState) && !FlashRobotUtil.inEmergencyStop() && !stop){
					FlashUtil.delay(ITERATION_DELAY);
				}
			}
		}
	}
	
	@Override
	protected void configInit(RobotInitializer initializer){
		SimpleRobotInitializer ainitializer = new SimpleRobotInitializer();
		preInit(ainitializer);
		
		initializer.copy(ainitializer);
	}
	@Override
	protected void robotMain() {
		robotInit();
		robotLoop();
	}
	@Override
	protected void robotShutdown(){
		stop = true;
		robotFree();
	}
	
	protected void preInit(SimpleRobotInitializer initializer){}
	protected abstract void robotInit();
	protected void robotFree(){}
	
	protected abstract void disabled();
	
	protected abstract void onMode(int mode);
}
