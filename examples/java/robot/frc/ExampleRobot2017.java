package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.robot.InstantAction;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.rio.FlashRio;
import edu.flash3388.flashlib.robot.rio.RioControllers;
import edu.flash3388.flashlib.robot.systems.MecanumDrive;
import edu.flash3388.flashlib.robot.systems.SingleMotorSystem;

import edu.wpi.first.wpilibj.VictorSP;

/*
 * An example for a simple 2017 FRC robot. The robot has a mecanum drive, a firing system with one motor and
 * a climbing system with one motor.
 * 
 * This time we use the FlashRio base, meaning that the FlashLib scheduler is operational.
 */
public class ExampleRobot2017 extends FlashRio{

	MecanumDrive driveTrain;
	SingleMotorSystem firingSystem;
	SingleMotorSystem climbingSystem;
	
	XboxController controller;
	
	/*
	 * preInit allows control of initialization settings for FlashRio. It is not mandatory to implement
	 * it, but possible.
	 */
	@Override
	protected void preInit(RobotInitializer initializer) {
		/*
		 * Enables the robot logs to be used. The robot logs are both the default flashlib test which is used
		 * by several features throughout flashlib. In addition to the default log, there is the robot
		 * power log which tracks data issues with the power supply of the robot.
		 * By default, this feature is disabled.
		 */
		//initializer.logsEnabled = true;
		
		/*
		 * Sets whether or not to enable the power log. This can be used when the use of the main flashlib
		 * log is wanted but not the use of the power log. If the powerlog was not enabled through 'enableLogs()'
		 * this will do nothing.
		 */
		//setPowerLogging(log);
		//initializer.logPower = true;
		
		/*
		 * Sets the total current draw from the PDP which will warrant a log into the power log.
		 * As soon as the PDP total current draw exceeds this value, a warning will be logged.
	     * The default is 80 Ampere.
		 */
		//setPowerDrawWarning(current);
		//initializer.warningPowerDraw = 100.0;
		
		/*
		 * Sets the PDP voltage level which will warrant logging into the power log. As soon
		 * as the PDP voltage drops below this value, a warning will be logged.
		 * The default is 8.0 volts.
		 */
		//setVoltageDropWarning(volts);
		//initializer.warningVoltage = 6.0;
		
		/*
		 * Sets the flashboard to use UDP protocol and not TCP (which is the default). You must make sure
		 * the flashboard software is set to use UDP or they won't be able to connect to each other.
		 */
		 //Flashboard.setProtocolUdp();
	}
	@Override
	protected void initRobot() {
		/*
		 * Creates all the speed controllers used for the drive system.
		 * Luckily for us, we use TalonSRX controllers that are connected through CANBus.
		 */
		CANTalon frontRight = new CANTalon(0);
		CANTalon frontLeft = new CANTalon(1);
		CANTalon rearRight = new CANTalon(2);
		CANTalon rearLeft = new CANTalon(3);
		
		/*
		 * Creates the speed controllers for the other systems: firing and climbing.
		 * Using VictorSP controllers.
		 */
		VictorSP firingController = new VictorSP(0);
		VictorSP climbingController = new VictorSP(1);
		
		/*
		 * Creating the mecanum drive. Wrapping each controller into a RioControllers object.
		 */
		driveTrain = new MecanumDrive(
				new RioControllers(frontRight), 
				new RioControllers(rearRight), 
				new RioControllers(frontLeft), 
				new RioControllers(rearLeft)
		);
		/*
		 * Sets the default action of the drive train. Executes when the drive train has no other action.
		 */
		driveTrain.setDefaultAction(new SystemAction(new Action(){
			@Override
			protected void execute() {
				/*
				 * Drives the drive train using the xbox controller.
				 * Uses the left stick for vector control. Uses the right stick for rotation control.
				 */
				driveTrain.mecanumDrive_polar(controller.LeftStick, controller.RightStick);
			}
			@Override
			protected void end() {
				/*
				 * Stops the drive train
				 */
				driveTrain.stop();
			}
		}, driveTrain));
		
		/*
		 * Creates the firing system. Wraps the speed controller in a RioControllers object
		 */
		firingSystem = new SingleMotorSystem(
				new RioControllers(firingController)
		);
		/*
		 * Sets the default action of the firing system. 
		 */
		firingSystem.setDefaultAction(new SystemAction(new Action(){
			@Override
			protected void execute() {
				/*
				 * Activates the firing system depending on the value of the right trigger 
				 * of the controller.
				 */
				firingSystem.set(controller.Triggers.Right.getValue());
			}
			@Override
			protected void end() {
				/*
				 * Stops the firing system
				 */
				firingSystem.stop();
			}
		}, firingSystem));
		
		/*
		 * Creates the climbing system, Wraps the speed controller into a RioControllers object.
		 */
		climbingSystem = new SingleMotorSystem(
				new RioControllers(climbingController)
		);
		
		/*
		 * Creating an xbox controller at index 0
		 */
		controller = new XboxController(0);
		
		/*
		 * When A is pressed on the controller, an instant action is executed. That action cancels all
		 * actions running on the systems.
		 */
		controller.A.whenPressed(new InstantAction(){
			@Override
			protected void execute() {
				driveTrain.cancelCurrentAction();
				firingSystem.cancelCurrentAction();
				climbingSystem.cancelCurrentAction();
			}
		});
		/*
		 * When B is pressed on the controller, an action is executed which rotates the motor controlled
		 * by the climbing system, basically causes the robot to climb.
		 */
		controller.B.whenPressed(climbingSystem.FORWARD_ACTION);
	}

	@Override
	protected void teleopInit() {
	}
	@Override
	protected void teleopPeriodic() {
	}

	@Override
	protected void autonomousInit() {}
	@Override
	protected void autonomousPeriodic() {}

	@Override
	protected void disabledInit() {
	}
	@Override
	protected void disabledPeriodic() {
	}

}
