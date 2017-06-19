package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.robot.InstantAction;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
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
		driveTrain.setDefaultAction(new SystemAction(driveTrain, new Action(){
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
		}));
		
		/*
		 * Creates the firing system. Wraps the speed controller in a RioControllers object
		 */
		firingSystem = new SingleMotorSystem(
				new RioControllers(firingController)
		);
		/*
		 * Sets the default action of the firing system. 
		 */
		firingSystem.setDefaultAction(new SystemAction(firingSystem, new Action(){
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
		}));
		
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
		/*
		 * Updates the controllers. Used to refresh the buttons on controllers so that actions attached to
		 * them will be executed when needed.
		 */
		FlashRoboUtil.updateHID();
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
