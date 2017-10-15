package examples.robot.frc;

import edu.flash3388.flashlib.robot.frc.FRCSpeedControllers;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.systems.FlashDrive;
import edu.flash3388.flashlib.util.FlashUtil;

import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.TalonSRX;

/*
 * In this example we will review usage of the FlashDrive class
 * for driving a robot with tank drive system using an xbox controller.
 */
public class ExampleTankDrive extends SampleRobot{

	//The FlashDrive object.
	//This is a subsystem which provides several control types
	//for several different drive systems
	FlashDrive driveTrain;
	
	//The XboxController object. 
	//We will use this controller to control our drive system.
	XboxController controller;
	
	protected void robotInit() {
		//We need to create the speed controllers for our drive system.
		//In this example, we have a 4x4 tank drive, so we create 4 speed 
		//controller objects from WPILib. We will use the TalonSRX speed controllers.
		
		TalonSRX frontRight = new TalonSRX(0);
		TalonSRX rearRight = new TalonSRX(1);
		TalonSRX frontLeft = new TalonSRX(2);
		TalonSRX rearLeft = new TalonSRX(3);
		
		//Since FlashLib wasn't specifically built for FRC, 
		//FlashDrive cannot receive WPILib speed controller objects.
		//We need to create a FlashSpeedController object to wrap the speed 
		//controllers. We will use FRCSpeedControllers. This class can receive
		//several speed controller objects. 
		//We will create 2 wrappers: one for the left side, another for the right side
		
		FRCSpeedControllers rightControllers = new FRCSpeedControllers(frontRight, rearRight);
		FRCSpeedControllers leftControllers = new FRCSpeedControllers(frontLeft, rearLeft);
		
		//Now we can create the FlashDrive object and pass it or speed controller
		//wrappers.
		driveTrain = new FlashDrive(rightControllers, leftControllers);
		
		//Creating our controller for channel 0 of the DriverStation.
		controller = new XboxController(0);
    }
	
	public void operatorControl() {
		while(isOperatorControl() && isEnabled()){
			//To drive our drive system using the tank drive motion, we call
			//the tankDrive method and pass it motion arguments for each side
			//of the system.
			//We pass the method Y-axis values from each of the controller's sticks.
			driveTrain.tankDrive(controller.RightStick.getY(), controller.LeftStick.getY());
			
			/*
			 * 5 millisecond delay
			 */
			FlashUtil.delay(5);
		}
    }
}
