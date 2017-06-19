package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.flashboard.DashboardInput;
import edu.flash3388.flashlib.flashboard.DoubleProperty;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.InputType;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.InstantAction;
import edu.flash3388.flashlib.robot.SystemAction;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.rio.FlashRio;
import edu.flash3388.flashlib.robot.rio.RioControllers;
import edu.flash3388.flashlib.robot.systems.MecanumDrive;
import edu.flash3388.flashlib.util.ConstantsHandler;
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
	
	DoubleDataSource driveFilterSpeed = ConstantsHandler.putNumber("filterSpeed", 1.0);
	
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
		driveTrain.setDefaultAction(new SystemAction(driveTrain, new Action(){
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
		}));
		
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
		 * Creates flashboard double properties. Creates double data sources pointing to the ultrasonics.
		 */
		DoubleProperty propSonic1 = new DoubleProperty("sonic1", ()->sonic1.getRangeMM());
		DoubleProperty propSonic2 = new DoubleProperty("sonic2", ()->sonic2.getRangeMM());
		/*
		 * Creates an input from flashboard to update our filterSpeed. It updates it directly through
		 * ConstantsHandler.
		 */
		DashboardInput inputFilter = new DashboardInput("filterSpeed", InputType.Double);
		
		/*
		 * Attaches the properties to the flashboard.
		 */
		Flashboard.attach(propSonic1, propSonic2, inputFilter);
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
