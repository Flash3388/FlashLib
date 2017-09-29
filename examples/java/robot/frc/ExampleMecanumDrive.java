package examples.robot.frc;

import edu.flash3388.flashlib.robot.frc.FRCSpeedControllers;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.systems.MecanumDrive;
import edu.flash3388.flashlib.util.FlashUtil;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.TalonSRX;

/*
 * In this example we will review usage of the MecanumDrive class
 * for driving a robot with mecanum drive system using an xbox controller.
 */
public class ExampleMecanumDrive extends SampleRobot{

	//The MecanumDrive object.
	//This is a subsystem which provides control
	//of a mecanum drive.
	MecanumDrive driveTrain;
	
	//The XboxController object. 
	//We will use this controller to control our drive system.
	XboxController controller;
	
	protected void robotInit() {
		//We need to create the speed controllers for our drive system.
		//In this example, we have a mecanum drive, so we create 4 speed 
		//controller objects from WPILib. We will use the TalonSRX speed controllers.
		
		TalonSRX frontRight = new TalonSRX(0);
		TalonSRX rearRight = new TalonSRX(1);
		TalonSRX frontLeft = new TalonSRX(2);
		TalonSRX rearLeft = new TalonSRX(3);
		
		//Since FlashLib wasn't specifically built for FRC, 
		//MecanumDrive cannot receive WPILib speed controller objects.
		//We need to create a FlashSpeedController object to wrap the speed 
		//controllers. We will use FRCSpeedControllers. This class can receive
		//several speed controller objects. 
		//We will create 4 wrappers: one for each controller
		
		FRCSpeedControllers frontRightWrapper = new FRCSpeedControllers(frontRight);
		FRCSpeedControllers rearRightWrapper = new FRCSpeedControllers(rearRight);
		FRCSpeedControllers frontLeftWrapper = new FRCSpeedControllers(frontLeft);
		FRCSpeedControllers rearLeftWrapper = new FRCSpeedControllers(rearLeft);
		
		//Now we can create the MecanumDrive object and pass it or speed controller
		//wrappers.
		driveTrain = new MecanumDrive(frontRightWrapper, rearRightWrapper, frontLeftWrapper, rearLeftWrapper);
		
		//Creating our controller for channel 0 of the DriverStation.
		controller = new XboxController(0);
    }
	
	public void operatorControl() {
		while(isOperatorControl() && isEnabled()){
			//We have 2 basic ways to control our system: using a polar vector or
			//cartesian vector. In this example we will use a polar vector for control.
			//We will pass arguments from our controller. The vector magnitude will be
			//the magnitude of the right stick and vector direction will be the right stick
			//direction. The mecanum drive rotation will be received from the left stick x-axis
			driveTrain.mecanumDrive_polar(controller.RightStick.getMagnitude(), controller.RightStick.getAngle()
					, controller.LeftStick.getX());
			
			/*
			 * 5 millisecond delay
			 */
			FlashUtil.delay(5);
		}
    }
}
