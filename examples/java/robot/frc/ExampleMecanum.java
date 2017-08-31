package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.robot.frc.FRCSpeedControllers;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.systems.MecanumDrive;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.wpi.first.wpilibj.SampleRobot;

/*
 * Example for a simple robot with a mecanum drive controlled by an Xbox controller.
 */
public class ExampleMecanum extends SampleRobot{
	
	MecanumDrive driveTrain;
	
	XboxController controller;
	
	protected void robotInit() {
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
				new FRCSpeedControllers(frontRight), 
				new FRCSpeedControllers(rearRight), 
				new FRCSpeedControllers(frontLeft), 
				new FRCSpeedControllers(rearLeft)
		);
		
		/*
		 * Creating an xbox controller at index 0
		 */
		controller = new XboxController(0);
    }
	
	public void operatorControl() {
		while(isOperatorControl() && isEnabled()){
			/*
			 * Drives the drive train using the xbox controller.
			 * Uses the left stick for vector control. Uses the right stick for rotation control.
			 */
			driveTrain.mecanumDrive_polar(controller.LeftStick, controller.RightStick);
			
			/*
			 * 5 millisecond delay
			 */
			FlashUtil.delay(5);
		}
    }
}
