package examples.robot;

import edu.flash3388.flashlib.robot.sbc.SimpleRobot;
import edu.flash3388.flashlib.util.FlashUtil;

/*
 * In this example we will be reviewing the SimpleRobot base provided by FlashLib 
 * for use in robot control. This example will not contain actual robot code, just simple 
 * methods and comments.
 */
public class ExampleSimpleRobot extends SimpleRobot{

	/*
	 * A constant value for manual control of the robot. The value used and the mode
	 * itself is chosen by the user, the only pre-made mode is disabled.
	 */
	public static final int OPERATION_MODE_MANUAL = 0x1;
	/*
	 * A constant value for automatic control of the robot. The value used and the mode
	 * itself is chosen by the user, the only pre-made mode is disabled.
	 */
	public static final int OPERATION_MODE_AUTO = 0x2;
	
	@Override
	protected void preInit(SimpleRobotInitializer initializer) {
		/*
		 * preInit is used by SimpleRobot to allow users to customize FlashLib operations.
		 * The received class contains simple variable, we can edit those to change initialization 
		 * parameters for FlashLib.
		 * The default implementation of this method is empty, so override it only when custom initialization
		 * is wanted.
		 */
	}
	@Override
	protected void robotInit() {
		/*
		 * robotInit is called as soon as all FlashLib systems are ready for use. So here we should
		 * perform initialization to our robot systems. It is important that initialization to system
		 * does not occur before this method is called because FlashLib might not be ready yet.
		 */
	}
	@Override
	protected void robotFree() {
		/*
		 * robotFree is used when the software is stopped. When the JVM enters shutdown, FlashLib will
		 * perform an ordered shutdown and will call this method first for user shutdown. Users should
		 * free systems used here (if needed).
		 * This method has a default empty implementation, so override it only when needed.
		 */
	}

	@Override
	protected void disabled() {
		/*
		 * Called when the robot enters disabled mode. This method is not called periodically, it is
		 * called once and while the robot is in disabled, users should remain in it. If this method ends
		 * before disabled mode has ended than no user code will be executed for the rest of the mode.
		 * 
		 * Because of that, it is recommended to create a loop here which runs while the robot is in disabled
		 * in order to execute periodic tasks. 
		 */
		
		/*
		 * We should start by performing initialization for the disabled mode, we should prepare our
		 * robot for idle mode which means disabling and stopping operation of systems.
		 */
		
		//some init code
		
		/*
		 * A loop which runs while the robot is in disabled mode. If this loop won't stop when the robot exits
		 * disabled, we will end up disrupting robot operations.
		 * isDisabled() is an inherited method which returns whether or not the current mode is disabled.
		 */
		while (isDisabled()) {
			/*
			 * Generally no code should be here since disabled mode should insure that the robot does nothing
			 * thus making it safe. But if wanted this could be used for things like data updates, but actuators
			 * should not be operated.
			 */
			
			//it is recommended to put perform a small ~5ms delay to the current thread. It allows
			//background operation to finish execute
			FlashUtil.delay(5);
		}
	}
	@Override
	protected void onMode(int mode) {
		/*
		 * Called when the robot enters a mode which is not disabled. This method is not called periodically, 
		 * it is called once and while the robot is in the mode, users should remain in it. If this method 
		 * ends before mode has ended than no user code will be executed for the rest of the mode.
		 * 
		 * Because of that, it is recommended to create a loop here which runs while the robot is in the mode
		 * in order to execute periodic tasks. 
		 * 
		 * For easier use, we will direct the method to custom methods created especially to handle our
		 * different operation modes. 
		 */
		
		switch(mode){
			case OPERATION_MODE_MANUAL:
				manual();
				break;
			case OPERATION_MODE_AUTO:
				auto();
				break;
		}
	}
	
	
	private void manual(){
		/*
		 * A loop which runs while the robot is in manual mode. If this loop won't stop when the robot exits
		 * disabled, we will end up disrupting robot operations.
		 * isMode() is an inherited method which returns whether or not the given value is the current mode.
		 */
		while (isMode(OPERATION_MODE_MANUAL)) {
			
			/*
			 * Here we will perform periodic operations for the `manual` operation mode. 
			 */
			
			//it is recommended to put perform a small ~5ms delay to the current thread. It allows
			//background operation to finish execute
			FlashUtil.delay(5);
		}
	}
	
	private void auto(){
		/*
		 * A loop which runs while the robot is in auto mode. If this loop won't stop when the robot exits
		 * disabled, we will end up disrupting robot operations.
		 * isMode() is an inherited method which returns whether or not the given value is the current mode.
		 */
		while (isMode(OPERATION_MODE_AUTO)) {
			
			/*
			 * Here we will perform periodic operations for the `auto` operation mode. 
			 */
			
			//it is recommended to put perform a small ~5ms delay to the current thread. It allows
			//background operation to finish execute
			FlashUtil.delay(5);
		}
	}
}
