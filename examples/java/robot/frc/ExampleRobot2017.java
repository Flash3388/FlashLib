package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.robot.InstantAction;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.frc.FRCSpeedControllers;
import edu.flash3388.flashlib.robot.frc.IterativeFRCRobot;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.systems.MecanumDrive;
import edu.flash3388.flashlib.robot.systems.SingleMotorSystem;
import edu.flash3388.flashlib.robot.systems.Systems;

import edu.wpi.first.wpilibj.VictorSP;

/*
 * An example for a simple 2017 FRC robot. The robot has a mecanum drive, a firing system with one motor and
 * a climbing system with one motor.
 * 
 * This time we use the FlashRio base, meaning that the FlashLib scheduler is operational.
 */
public class ExampleRobot2017 extends IterativeFRCRobot{

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
		 * Enables the robot logs to be used. This allows flashlib operations to log data into
		 * the main flashlib log.
		 */
		//initializer.standardLogs = true;
		
		/*
		 * Sets whether or not to enable the power log. This allows the control loop
		 * to track power issues in voltage and power draw and log them.
		 */
		//initializer.logPower = true;
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
				new FRCSpeedControllers(frontRight), 
				new FRCSpeedControllers(rearRight), 
				new FRCSpeedControllers(frontLeft), 
				new FRCSpeedControllers(rearLeft)
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
				new FRCSpeedControllers(firingController)
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
				firingSystem.set(controller.RT.get());
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
				new FRCSpeedControllers(climbingController)
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
		controller.B.whenPressed(Systems.forwardAction(climbingSystem, 0.8));
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
