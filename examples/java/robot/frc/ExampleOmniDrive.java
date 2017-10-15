package examples.robot.frc;

import edu.flash3388.flashlib.robot.frc.FRCSpeedControllers;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.systems.FlashDrive;
import edu.flash3388.flashlib.util.FlashUtil;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.TalonSRX;

/*
 * In this example we will review usage of the FlashDrive class
 * for driving a robot with an omni drive system using an xbox controller.
 */
public class ExampleOmniDrive extends SampleRobot{

	//The FlashDrive object.
	//This is a subsystem which provides several control types
	//for several different drive systems
	FlashDrive driveTrain;
	
	//The XboxController object. 
	//We will use this controller to control our drive system.
	XboxController controller;
	
	protected void robotInit() {
		//We need to create the speed controllers for our drive system.
		//In this example, we have an omni drive with 4 motors:
		//one on the right side and one on the left side (moving the robot along the Y axis)
		//one on the front of the robot and one on the rear of the robot (moving the robot along the X axis)
		//so we create 4 speed controller objects from WPILib. We will use the TalonSRX speed controllers.
		
		TalonSRX front = new TalonSRX(0);
		TalonSRX rear = new TalonSRX(1);
		TalonSRX left = new TalonSRX(2);
		TalonSRX right = new TalonSRX(3);
		
		//Since FlashLib wasn't specifically built for FRC, 
		//FlashDrive cannot receive WPILib speed controller objects.
		//We need to create a FlashSpeedController object to wrap the speed 
		//controllers. We will use FRCSpeedControllers. 
		//We will create 4 wrappers: one for each side
		
		FRCSpeedControllers frontWrapper = new FRCSpeedControllers(front);
		FRCSpeedControllers rearWrapper = new FRCSpeedControllers(rear);
		FRCSpeedControllers rightWrapper = new FRCSpeedControllers(right);
		FRCSpeedControllers leftWrapper = new FRCSpeedControllers(left);
		
		//Now we can create the FlashDrive object and pass it or speed controller
		//wrappers.
		driveTrain = new FlashDrive(rightWrapper, leftWrapper, frontWrapper, rearWrapper);
		
		//Creating our controller for channel 0 of the DriverStation.
		controller = new XboxController(0);
    }
	
	public void operatorControl() {
		while(isOperatorControl() && isEnabled()){
			//To drive our drive system using the omni drive motion, we call
			//the omniDrive method and pass it motion arguments for each motion axis: y, x.
			//We pass the method the Y-axis value and X-axis value of the controller's right stick
			driveTrain.omniDrive(controller.RightStick.getY(), controller.RightStick.getX());
			
			/*
			 * 5 millisecond delay
			 */
			FlashUtil.delay(5);
		}
    }
}
