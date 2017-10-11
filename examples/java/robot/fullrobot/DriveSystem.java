package examples.robot.fullrobot;

import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.IOFactory;
import edu.flash3388.flashlib.robot.devices.PWM;
import edu.flash3388.flashlib.robot.devices.Talon;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;

/*
 * Our robot's drive system. 
 * 
 * We have a 2-motor (1 for each side) tank style drive. 
 * 
 * This class extends Subsystem so we can use it with the scheduling system.
 * 
 * Because this is a tank drive system, we implement the TankDriveSystem to receive 
 * a built control for our system.
 * 
 * For our speed controllers, we use FlashLib's Talon class.
 * 
 * Our PWM ports will be retrieved from the IOFactory.
 */
public class DriveSystem extends Subsystem implements TankDriveSystem{

	//our right side speed controller
	private FlashSpeedController controllerR;
	//our left side speed controller
	private FlashSpeedController controllerL;
	
	public DriveSystem() {
		//initialize the PWM port for the right speed controller.
		//the port is saved as a constant in RobotMap 
		PWM pwmR = IOFactory.createPWMPort(RobotMap.DRIVE_RIGHT);
		//initialize the PWM port for the left speed controller.
		//the port is saved as a constant in RobotMap 
		PWM pwmL = IOFactory.createPWMPort(RobotMap.DRIVE_LEFT);
		
		//initialize our right speed controller
		controllerR = new Talon(pwmR);
		//initialize our left speed controller
		controllerL = new Talon(pwmL);
	}
	
	@Override
	public void tankDrive(double right, double left) {
		//set the right speed to our right motor
		controllerR.set(right);
		//set the left speed to our left motor
		controllerL.set(left);
	}
		
	@Override
	public void stop() {
		//stopping our right speed controller
		controllerR.stop();
		//stopping our left speed controller
		controllerL.stop();
	}
}
