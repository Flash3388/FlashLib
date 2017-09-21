package examples.robot;

import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.PWM;
import edu.flash3388.flashlib.robot.devices.Talon;
import edu.flash3388.flashlib.robot.hal.HALPWM;
import edu.flash3388.flashlib.robot.systems.Rotatable;

/*
 * In this example we will be reviewing creation of custom subsystem for our robot as part
 * of FlashLib's scheduling system. 
 * 
 * As explained in our Wiki, a subsystem represents a system
 * on a robot which can be used separately from other parts of the robot. Examples for subsystems include
 * but are not limited to: drive trains, arms, shooters, etc.
 * 
 * The concept of what makes a part of a robot into a subsystem depends on the way you wish
 * to organize you code, but in general remains the same.
 * 
 * In this example we will be showcasing a simple one-motor arm.
 * 
 * To start, we need to extend the Subsystem class. 
 * 
 * The we need to define the properties of the subsystem. Since our arm is controlled by a single motor, 
 * we will define a FlashSpeedController variable. We will initialize this object in our constructor.
 * 
 * Then we need to create methods which will move our arm. To help FlashLib provides interfaces for
 * subsystems, making sure users have appropriate operation methods. We will implement the Rotatable interface.
 */
public class ExampleCustomSubsystem extends Subsystem implements Rotatable{

	/*
	 * Our speed controller object. FlashSpeedController is an interface, so
	 * we can use any implementation for it.
	 */
	private FlashSpeedController speedController;
	
	public ExampleCustomSubsystem() {
		/*
		 * Here we should create an prepare our speed controller for use. Since
		 * it which is used depends on the robot and platform, here we will 
		 * use a PWM speed controller called Talon, and use FlashLib's HAL to control it.
		 * Normally you would need to insure you platform has PWM ports and an HAL implementation
		 * is available for it.
		 */
		
		/*
		 * Initializing our PWM port. Let's use port 0, this will refer to different ports
		 * depending on the used platform.
		 */
		PWM pwmPort = new HALPWM(0);
		
		/*
		 * Let's create a Talon speed controller using the PWM port we just initialized. Note that
		 * FlashLib already provides a class for it.
		 */
		speedController = new Talon(pwmPort);
	}
	
	/*
	 * The rotate method executes rotation, basically we rotate our arm. It should receive a value
	 * between -1 and 1 indicating the speed and direction of the rotation. 
	 * FlashLib views speed as a value between -1 and 1, so you should keep using it. 
	 */
	@Override
	public void rotate(double speed) {
		/*
		 * Setting our speed controller to rotate at the given speed.
		 */
		speedController.set(speed);
	}
	/*
	 * stop is important in order to insure that the motor thus not run when not necessary. It is necessary
	 * to use it in order to make sure the motor does not keep running when not needed. Safety is important.
	 */
	@Override
	public void stop() {
		/*
		 * Call the stop method of the speed controller.
		 */
		speedController.stop();
	}
}
