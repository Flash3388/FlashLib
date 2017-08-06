package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.InstantAction;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.rio.FlashRio;
import edu.flash3388.flashlib.robot.rio.RioControllers;
import edu.flash3388.flashlib.robot.systems.MecanumDrive;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.wpi.first.wpilibj.Ultrasonic;

/*
 * A simple robot with a mecanum drive controlled by an xbox controller, and 2 ultrasonics.
 * Shows the ultasonic data on the flashboard. Uses input from flashboard to change a filter for our
 * drive system.
 */
public class ExampleFlashboard extends FlashRio{

	MecanumDrive driveTrain;
	
	XboxController controller;
	
	Ultrasonic sonic1;
	Ultrasonic sonic2;
	
	DoubleProperty driveFilterSpeed = ConstantsHandler.putNumber("filterSpeed", 1.0);
	
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
				 * Uses the filter source to decrease the values by multiplying rotation an magnitude by
				 * it.
				 */
				double magnitude = controller.LeftStick.getMagnitude() * driveFilterSpeed.get();
				double angle = controller.LeftStick.getAngle();
				double rotation = controller.RightStick.getX() * driveFilterSpeed.get();
				driveTrain.mecanumDrive_polar(magnitude, angle, rotation);
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
		 * When A is pressed on the controller, an instant action is executed. That action cancels all
		 * actions running on the systems.
		 */
		controller.A.whenPressed(new InstantAction(){
			@Override
			protected void execute() {
				driveTrain.cancelCurrentAction();
			}
		});
		
		/*
		 * Creates ultrasonic sensors from WPILib 
		 */
		sonic1 = new Ultrasonic(0, 1);
		sonic2 = new Ultrasonic(2, 3);
		/*
		 * Sets those sensors to automatic update mode.
		 */
		sonic1.setAutomaticMode(true);
		
		/*
		 * Creates and attaches flashboard double properties. Creates double data sources pointing to the ultrasonics.
		 */
		Flashboard.putData("sonic1", ()->sonic1.getRangeMM());
		Flashboard.putData("sonic1", ()->sonic2.getRangeMM());
		
		/*
		 * Creates a slider infput from flashboard to update our filterSpeed. It updates our property directly.
		 * The slider as a minimum value of 0.0, a maximum value of 1.0 and the default change of values is
		 * 0.1 ((1.0 - 0.0) / 10).
		 */
		Flashboard.putSlider("Drive Filter", driveFilterSpeed, 0.0, 1.0, 10);
		
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
