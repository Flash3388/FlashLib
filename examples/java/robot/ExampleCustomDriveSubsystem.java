package examples.robot;

import edu.flash3388.flashlib.robot.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.devices.PWM;
import edu.flash3388.flashlib.robot.devices.Talon;
import edu.flash3388.flashlib.robot.hal.HALPWM;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;

/*
 * In this example we will be reviewing creation of custom drive subsystem for our robot as part
 * of FlashLib's scheduling system. 
 * 
 * As explained in our Wiki, a subsystem represents a system
 * on a robot which can be used separately from other parts of the robot. Examples for subsystems include
 * but are not limited to: drive trains, arms, shooters, etc.
 * 
 * The concept of what makes a part of a robot into a subsystem depends on the way you wish
 * to organize you code, but in general remains the same.
 * 
 * In this example we will be showcasing a simple tank drive system.
 * 
 * To start, we need to extend the Subsystem class. 
 * 
 * The we need to define the properties of the subsystem. Since our arm is controlled by a single motor, 
 * we will define a FlashSpeedController variable. We will initialize this object in our constructor.
 * 
 * Then we need to create methods which will use our subsystem.
 * 
 * FlashLib provides several interfaces for subsystem which provide a template for the system, 
 * allow usage of built-in actions and some even provide extra control method with default implementations
 * which makes creation of systems easier. It is highly recommended to use those interfaces.
 * In this example we will use the TankDriveSystem interface.
 */
public class ExampleCustomDriveSubsystem implements TankDriveSystem{

	/*
	 * Our speed controller objects. FlashSpeedController is an interface, so
	 * we can use any implementation for them. We have 2 controllers: 1 for 
	 * each side of the robot.
	 */
	private FlashSpeedController controllerRight;
	private FlashSpeedController controllerLeft;
	
	
	public ExampleCustomDriveSubsystem() {
		/*
		 * Here we should create an prepare our subsystem for use. We will create our
		 * speed controllers here, since which are used depends on the robot and platform, here we will 
		 * use PWM speed controllers called Talon, and use FlashLib's HAL to control it.
		 * Normally you would need to insure you platform has PWM ports and an HAL implementation
		 * is available for it.
		 */
		
		/*
		 * Initializing our PWM ports. Let's use ports 0 and 1, this will refer to different ports
		 * depending on the used platform.
		 */
		PWM portRight = new HALPWM(0);
		PWM portLeft = new HALPWM(1);
		
		/* Let's create a Talon speed controllers using the PWM ports we just initialized. Note that
		 * FlashLib already provides a class for it.
		 */
		controllerRight = new Talon(portRight);
		controllerLeft = new Talon(portLeft);
	}
	
	/*
	 * The tankDrive method executes motion for a tank-like drive system, basically it moves each side
	 * of the robot independently. It should receive values between -1 and 1 indicating the speed and 
	 * direction of each side. FlashLib views speed as a value between -1 and 1, so you should keep using it. 
	 */
	@Override
	public void tankDrive(double right, double left) {
		/*
		 * Setting our speed controllers to move at the given speed values.
		 */
		controllerLeft.set(left);
		controllerRight.set(right);
	}
	/*
	 * stop is important in order to insure that the motors don't run when not needed. It is necessary
	 * to use it in order to make sure the motors do not keep running when not needed. Safety is important.
	 */
	@Override
	public void stop() {
		/*
		 * Call the stop method of the speed controllers.
		 */
		controllerLeft.stop();
		controllerRight.stop();
	}
}
