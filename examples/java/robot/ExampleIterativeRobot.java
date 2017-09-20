package examples.robot;

import edu.flash3388.flashlib.robot.sbc.IterativeRobot;

/*
 * In this example we will be reviewing the IterativeRobot base provided by FlashLib 
 * for use in robot control. This example will not contain actual robot code, just simple 
 * methods and comments.
 */
public class ExampleIterativeRobot extends IterativeRobot{

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
	protected void preInit(RobotInitializer initializer) {
		/*
		 * preInit is used by IterativeRobot to allow users to customize FlashLib operations.
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
	protected void disabledInit() {
		/*
		 * disabledInit is called when the robot enters disabled mode. Here we should prepare our
		 * robot for idle mode which means disabling and stopping operation of systems.
		 */
	}
	@Override
	protected void disabledPeriodic() {
		/*
		 * This function is called periodically (~10ms) while the robot is in disabled mode.
		 * Generally no code should be here since disabled mode should insure that the robot does nothing
		 * thus making it safe. But if wanted this method could be used for things like data updates, but actuators
		 * should not be operated.
		 */
	}

	@Override
	protected void modeInit(int mode) {
		/*
		 * modeInit is called when the robot enters a new mode (not disabled) and receives the byte which
		 * indicates which mode this is. Initialization code for the given mode should be executed here. 
		 * Since FlashLib supports user-defined operation mode, it is necessary to choose values to
		 * indicate each mode. Here we will use the constants defined above and call external methods
		 * for actual execution.
		 */
		switch(mode){
			case OPERATION_MODE_MANUAL:
				manualInit();
				break;
			case OPERATION_MODE_AUTO:
				autoInit();
				break;
		}
	}
	@Override
	protected void modePeriodic(int mode) {
		/*
		 * modePeriodic is called periodically (every ~10ms or so) and receives a byte indicating the current
		 * operation mode. When entering an operation mode, modeInit will be called first and then will modeInit
		 * be called. Operational code for the mode should be executed here.
		 * Since FlashLib supports user-defined operation mode, it is necessary to choose values to
		 * indicate each mode. Here we will use the constants defined above and call external methods
		 * for actual execution.
		 */
		switch(mode){
			case OPERATION_MODE_MANUAL:
				manualPeriodic();
				break;
			case OPERATION_MODE_AUTO:
				autoPeriodic();
				break;
		}
	}
	
	
	private void manualInit(){
		/*
		 * This method will perform initialization for the `manual` operation mode. It will be called
		 * by modeInit.
		 */
	}
	private void manualPeriodic(){
		/*
		 * This method will perform periodic operations for the `manual` operation mode. It will be called 
		 * by modePeriodic.
		 */
	}
	
	private void autoInit(){
		/*
		 * This method will perform initialization for the `auto` operation mode. It will be called
		 * by modeInit.
		 */
	}
	private void autoPeriodic(){
		/*
		 * This method will perform periodic operations for the `auto` operation mode. It will be called 
		 * by modePeriodic.
		 */
	}
}
