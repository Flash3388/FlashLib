package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.rio.RioControllers;
import edu.flash3388.flashlib.robot.systems.FlashDrive;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.wpi.first.wpilibj.SampleRobot;

/*
 * Example for a simple robot with a tank drive controlled by an Xbox controller.
 */
public class ExampleTank extends SampleRobot{
	
	FlashDrive driveTrain;
	
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
		 * Creating the drive. Wrapping controllers from the same side into a RioControllers object.
		 */
		driveTrain = new FlashDrive(
				new RioControllers(frontRight, rearRight), 
				new RioControllers(frontLeft, rearLeft)
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
			 * Uses the right stick to move the right side and the left stick to 
			 * move the left stick.
			 */
			driveTrain.tankDrive(controller.RightStick, controller.LeftStick);
			
			/*
			 * 5 millisecond delay
			 */
			FlashUtil.delay(5);
		}
    }
}
