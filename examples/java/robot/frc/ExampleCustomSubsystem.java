package examples.robot.frc;

import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.systems.Rotatable;

import edu.wpi.first.wpilibj.Talon;

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
 * we will define a motor controller variable. We will initialize this object in our constructor.
 * 
 * Then we need to create methods which will move our arm. To help FlashLib provides interfaces for
 * subsystems, making sure users have appropriate operation methods. We will implement the Rotatable interface.
 */
public class ExampleCustomSubsystem extends Subsystem implements Rotatable{

	//the speed controller for our system. we will use a Talon controller.
	private Talon speedController;
	
	public ExampleCustomSubsystem() {
		/*
		 * Here we should prepare our system for use. We will create 
		 * our speed controller.
		 */
		
		//create our speed controller. we will use port 0.
		speedController = new Talon(0);
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
		 * Stop our speed controller. calling set and passing 0.0.
		 */
		speedController.set(0.0);
	}
}
