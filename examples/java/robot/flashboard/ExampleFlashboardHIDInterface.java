package examples.robot.flashboard;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.FlashboardHIDInterface;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.robot.ManualModeSelector;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.robot.hid.XboxController;

/*
 * In this example we will look at the Flashboard HID interface.
 */
public class ExampleFlashboardHIDInterface extends IterativeRobot{

	//we will use a single custom operation mode which will 
	//indicate robot operation.
	public static final int MODE_OPERATION = 1;
	
	//create our mode selector. we will need one just to put us in operation mode and
	//not disabled.
	ManualModeSelector modeSelector = new ManualModeSelector();
	
	//create our HID interface.
	//This allows us to use controllers and joysticks to control
	//the robot. Data about the controllers is received using this interface.
	//The flashboard hid interface receives HID data from the flashboard.
	FlashboardHIDInterface hidInterface = new FlashboardHIDInterface();
	
	//we will use an xbox controller to control our drive system
	XboxController xbox;

	@Override
	protected void preInit(IterativeRobotInitializer initializer) {
		//indicate that we want to initialize flashboard
		initializer.initFlashboard = true;
		//since we have no use in the flashboard's camera server, we disable it
		initializer.flashboardInitData.initMode = Flashboard.INIT_COMM;
		
		//set our mode selector
		initializer.modeSelector = modeSelector;
		
		//set our hid interface
		initializer.hidInterface = hidInterface;
	}
	@Override
	protected void robotInit() {
		//attach our hid interface to flashboard
		hidInterface.attachToFlashboard();
		//the hid interface needs to be updated periodically
		//so we add it as an update task to the Scheduler
		Scheduler.getInstance().addTask(hidInterface);
		
		//create our xbox controller. It should be connected to channel 0 of
		//the hid interface
		xbox = new XboxController(0);
		
		
		//setting ourselves to operation mode so we could checkout the 
		//values of controllers. This is not recommended when using an
		//actual movable robot since we are forcing it to stay in operation
		//and not allowing it to enter disabled mode at any given time.
		modeSelector.setMode(MODE_OPERATION);
	}

	@Override
	protected void disabledInit() {
	}
	@Override
	protected void disabledPeriodic() {
	}

	@Override
	protected void modeInit(int mode) {
	}
	@Override
	protected void modePeriodic(int mode) {
		/*
		 * Here you can add code to print out HID data and
		 * see that the XboxController is functional. 
		 * 
		 * For example:
		 * System.out.println(xbox.A.get());
		 * 
		 * Now if you press the A button, it should print true.
		 */
	}
	
	@Override
	public boolean isOperatorControl() {
		/*
		 * This method indicates that our robot is controlled manually, by a user
		 * will controllers. By default it returns false, but it is necessary for it to
		 * return when our robot is controlled by user. This data will be used
		 * in background operations.
		 * In our robot we have 2 modes: disabled and operation. Operation mode is
		 * controlled by an operator 
		 */
		return isMode(MODE_OPERATION);
	}
}
