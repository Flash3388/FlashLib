package examples.robot;

import edu.flash3388.flashlib.robot.sbc.RobotBase;

/*
 * In this example we will be reviewing the RobotBase base provided by FlashLib 
 * for use in robot control. This example will not contain actual robot code, just simple 
 * methods and comments.
 */
public class ExampleRobotBase extends RobotBase{

	@Override
	protected void configInit(RobotInitializer initializer) {
		/*
		 * configInit is used by RobotBase to allow users to customize FlashLib operations.
		 * The received class contains simple variable, we can edit those to change initialization 
		 * parameters for FlashLib.
		 * The default implementation of this method is empty, so override it only when custom initialization
		 * is wanted.
		 */
	}
	
	@Override
	protected void robotMain() {
		/*
		 * robotMain is called as soon as RobotBase has finished initialization.
		 * This the "main" method of our robot. Since this is open for users to decide
		 * what to do, we can't really explain further. But basically, this given free rein 
		 * of the control of robot operations.
		 */
	}

	@Override
	protected void robotShutdown() {
		/*
		 * robotShutdown is used when the software is stopped. When the JVM enters shutdown, FlashLib will
		 * perform an ordered shutdown and will call this method first for user shutdown. Users should
		 * free systems used here (if needed).
		 * This method has a default empty implementation, so override it only when needed.
		 */
	}
}
