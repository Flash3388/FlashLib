package examples.robot.frc;

import com.ctre.CANTalon;

import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.robot.frc.FRCSpeedControllers;
import edu.flash3388.flashlib.robot.frc.FlashFRCUtil;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.systems.FlashDrive;
import edu.flash3388.flashlib.util.FlashUtil;

import edu.wpi.first.wpilibj.SampleRobot;

public class ExampleManualInit extends SampleRobot{
	
	FlashDrive driveTrain;
	
	XboxController controller;
	
	protected void robotInit() {
		
		/*
		 * Create initialization data for flashboard. Can be used to set
		 * parameters for custom initialization of flashboard, or just left alone for default initialization.
		 */
		FlashboardInitData flashboardInitData = new FlashboardInitData();
		/*
		 * Initializes flashlib for FRC usage. Since we passes init data for flashboard, flashboard will be
		 * initialized as well. If we wish to not initialize flashboard, we can simply pass null instead.
		 * 
		 * This is the default initialization for FRC and will use the default Robot interface implementation
		 * for FRC provided by RobotFactory. There is an overload which receives an implementation of Robot if 
		 * a more custom approach is wanted.
		 */
		FlashFRCUtil.initFlashLib(flashboardInitData);
		
		
		/*
		 * From here you can use you robot normally, as if you extended a base class for robots from 
		 * FlashLib. But, if you are using Flashboard, you would have to manually call Flashboard.start()
		 * when initialization is done to start Flashboard.
		 * Call it at the end of robotInit().
		 */
		
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
