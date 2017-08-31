package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.Tester;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.frc.FRCSpeedControllers;
import edu.flash3388.flashlib.robot.frc.IterativeFRCRobot;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.systems.FlashDrive;
import edu.flash3388.flashlib.robot.systems.FlashDrive.MotorSide;

/*
 * An example for using the flashboard motor tester and tracker. We will track the motors of a tank drive system
 * and see their current draw and voltage drop in real-time. Instead of tracking all 4 motors, we will track each
 * side as a single motor just as an example. Usually it's preferable to track all motors.
 */
public class ExampleMotorTester extends IterativeFRCRobot{

	FlashDrive driveTrain;
	
	XboxController controller;
	
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
		 * Creating the drive. Wrapping controllers from the same side into a RioControllers object.
		 */
		driveTrain = new FlashDrive(
				new FRCSpeedControllers(frontRight, rearRight), 
				new FRCSpeedControllers(frontLeft, rearLeft)
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
				driveTrain.tankDrive(controller.RightStick, controller.LeftStick);
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
		 * Creating an xbox controller at index 0
		 */
		controller = new XboxController(0);
		
		/*
		 * Creates a new flashboard motor tester to tracker motor functionality.
		 */
		Tester driveTester = new Tester("drive");
		/*
		 * Adds both right side motors which are wrapped in one speed controller container to the tester
		 */
		driveTester.addMotor("Right", driveTrain.getControllers(MotorSide.Right))
				.setCurrentSource(()->frontRight.getOutputCurrent())//uses the front one as a source of current data used
				.setVoltageSource(()->frontRight.getOutputVoltage());//uses the front one as a source of voltage data used
		/*
		 * Adds both left side motors which are wrapped in one speed controller container to the tester
		 */
		driveTester.addMotor("Right", driveTrain.getControllers(MotorSide.Left))
				.setCurrentSource(()->frontLeft.getOutputCurrent())//uses the front one as a source of current data used
				.setVoltageSource(()->frontLeft.getOutputVoltage());//uses the front one as a source of voltage data used
		
		/*
		 * Attaches the tester to the flashboard
		 */
		Flashboard.attach(driveTester);
		/*
		 * Starts the flashboard
		 */
		Flashboard.start();
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
