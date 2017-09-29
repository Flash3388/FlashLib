package examples.robot.frc;

import edu.flash3388.flashlib.robot.frc.IterativeFRCRobot;

/*
 * In this example we will be reviewing the IterativeFRCRobot base provided by FlashLib 
 * for use in FRC robot control. This example will not contain actual robot code, just simple 
 * methods and comments.
 */
public class ExampleIterativeFRCRobot extends IterativeFRCRobot{

	@Override
	protected void preInit(RobotInitializer initializer) {
		/*
		 * preInit is used by IterativeFRCRobot to allow users to customize FlashLib operations.
		 * The received class contains simple variable, we can edit those to change initialization 
		 * parameters for FlashLib.
		 * The default implementation of this method is empty, so override it only when custom initialization
		 * is wanted.
		 */
		
		/*
		 * Enables the robot logs to be used. This allows FlashLib operations to log data into
		 * the main FlashLib log.
		 * By default, standard logging is disabled. 
		 */
		//initializer.standardLogs = true;
		
		/*
		 * Sets whether or not to enable the power log. This allows the control loop
		 * to track power issues in voltage and power draw and log them.
		 * By default, power logging is disabled.
		 */
		//initializer.logPower = true;
		
		/*
		 * Sets whether or not to enable auto HID updates. FlashLib's HID buttons need
		 * to be refreshed to automatically activate actions attached. When allowed,
		 * an update tasks for HID buttons is added to FlashLib's scheduler. This updates buttons
		 * which are a part of controllers from the HID package.
		 * By default, this is enabled.
		 */
		//initializer.autoUpdateHid = false;
		
		/*
		 * Sets whether or not IterativeFRCRobot's robot loop should run the FlashLib
		 * scheduler. If this is disabled, the scheduling system will not function.
		 * By default, this is enabled.
		 */
		//initializer.runScheduler = false;
		
		/*
		 * Sets whether or not to initialize Flashboard. If disabled, Flashboard control will
		 * not be initialized and cannot be used.
		 * By default, Flashboard control is initialized.
		 */
		//initializer.initFlashboard = false;
		
		/*
		 * If Flashboard control is set to initialize, it is possible to edit the
		 * initialization parameters. This variable holds an object of FlashboardInitData.
		 * If the variable is null, Flashboard control will not be initialized.
		 */
		//initializer.flashboardInitData;
	}
	@Override
	protected void initRobot() {
		/*
		 * initRobot is called as soon as all FlashLib systems are ready for use. So here we should
		 * perform initialization to our robot systems. It is important that initialization to system
		 * does not occur before this method is called because FlashLib might not be ready yet.
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
		 * This method is called periodically (~10ms) while the robot is in disabled mode.
		 * Generally no code should be here since disabled mode should insure that the robot does nothing
		 * thus making it safe. But if wanted this method could be used for things like data updates, but actuators
		 * should not be operated.
		 */
	}

	@Override
	protected void teleopInit() {
		/*
		 * teleopInit is called when the robot enters teleop mode. Here we should prepare our
		 * robot for operator control.
		 */
	}
	@Override
	protected void teleopPeriodic() {
		/*
		 * This method is called periodically (~10ms) while the robot is in teleop mode. In here
		 * we can perform periodic operation for teleop mode.
		 */
	}

	@Override
	protected void autonomousInit() {
		/*
		 * autonomousInit is called when the robot enters autonomous mode. Here we should prepare our
		 * robot for autonomous control.
		 */
	}
	@Override
	protected void autonomousPeriodic() {
		/*
		 * This method is called periodically (~10ms) while the robot is in autonomous mode. In here
		 * we can perform periodic operation for autonomous mode.
		 */
	}
}
