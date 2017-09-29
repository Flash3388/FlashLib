package examples.robot;

import edu.flash3388.flashlib.robot.FlashboardModeSelector;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.robot.PIDController;
import edu.flash3388.flashlib.robot.PIDSource;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.DoubleProperty;

/*
 * In this example we will take a look at FlashLib's PID controller. 
 */
public class ExamplePIDController extends IterativeRobot{

	
	//we will use a single custom operation mode which will 
	//indicate robot operation.
	public static final int MODE_OPERATION = 1;
	
	//create our mode selector. 
	//we will use a flashboard mode selector which is basically a combo box
	//on the flashboard.
	FlashboardModeSelector modeSelector = new FlashboardModeSelector();
	
	//our PID controller object
	PIDController pidcontroller;
	
	//a set point for the PID controller
	DoubleProperty pidSetPoint = ConstantsHandler.putNumber("setpoint", 0.0);

	@Override
	protected void preInit(IterativeRobotInitializer initializer) {
		//set our mode selector
		initializer.modeSelector = modeSelector;
	}
	@Override
	protected void robotInit() {
		
		//define the operation modes to our mode selector.
		modeSelector.addOption("Operation", MODE_OPERATION);
		//attach our mode selector to flashboard
		modeSelector.attachToFlashboard();
		
		/*
		 * The PIDController class allows us to control a system using a PID control loop.
		 * We have 4 control parameters:
		 * -kp: Proportional
		 * -ki: integral
		 * -kd: differential
		 * -kf: feed forward
		 * 
		 * We can also provide different settings for the controller to improve the operation
		 * of the system in many ways, such as:
		 * - output limits
		 * - input limits
		 * - change limits
		 * We will not review those in this example.
		 * 
		 * The input of the system is defined in a PIDSource interface object.
		 * It has a single method: pidGet, which should return the current status
		 * of the system.
		 * 
		 * The set point is defined as a DoubleSource object from FlashLib's beans package.
		 * Using this, we can easily manipulate the value of the set point throughout the robot
		 * code.
		 * 
		 * The control parameters are defined as DoubleProperty objects from
		 * FlashLib's beans package. This way, we can remotely set their values throughout our
		 * robot project.
		 * 
		 * There are 2 constructor types: property constructor and variable constructor.
		 * Property constructor receives the DoubleProperty objects for the control parameters.
		 * Variable constructor receives an initial parameter values and creates the property objects
		 * by it self. 
		 */
		
		//Let's talk about the PIDSource object. This should point to a sensor of sorts which measures our
		//system's status. 
		//TODO: FIGURE OUT SYSTEM FEEDBACK FOR EXAMPLE
		PIDSource pidsource = null;
		
		//For the set point source, we will use a DoubleProperty (extends DoubleSource) created
		//from the constants handler: pidSetPoint
		
		//In this example we will use the variable constructor for our PIDController. Let's 
		//create it:
		pidcontroller = new PIDController(0.05, 0.0, 0.0, 0.0, pidSetPoint, pidsource);
		
		//TODO: SET OUTPUT LIMITS DEPENDING ON THE SYSTEM USED IN THE EXAMPLE
		
		//now we have our PID controller ready for use. 
		
	}

	@Override
	protected void disabledInit() {
	}
	@Override
	protected void disabledPeriodic() {
	}

	@Override
	protected void modeInit(int mode) {
		//it is important to reset the controller when using it each time. this
		//resets accumulated information from the last run:
		pidcontroller.reset();
		
		//we need to make sure the PID controller is enabled
		pidcontroller.setEnabled(true);
	}
	@Override
	protected void modePeriodic(int mode) {
		//we need to periodically use the PID controller to receive outputs.
		//calling the calculate method performs the PID calculation and returns an
		//output value to be used to fix our system.
		double pidvalue = pidcontroller.calculate();
		
		//TODO: PASS OUTPUT TO SYSTEM
	}
}
