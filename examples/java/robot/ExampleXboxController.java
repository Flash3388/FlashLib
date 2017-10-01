package examples.robot;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashboardHIDInterface;
import edu.flash3388.flashlib.robot.InstantAction;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.robot.ManualModeSelector;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.robot.hid.DoubleButton;
import edu.flash3388.flashlib.robot.hid.DoubleHIDButton;
import edu.flash3388.flashlib.robot.hid.XboxController;

/*
 * In this example we will review usage of the XboxController
 * class. We will use flashboard for operation mode selection 
 * and for HID data.
 */
public class ExampleXboxController extends IterativeRobot{

	//we will use a single custom operation mode which will 
	//indicate robot operation.
	public static final int MODE_OPERATION = 1;
	
	//we define a variable which will hold our XboxController object
	XboxController xbox;
	//a DoubleButton object
	DoubleButton doubleButton;
	
	//create our mode selector. we will need one just to put us in operation mode and
	//not disabled.
	ManualModeSelector modeSelector = new ManualModeSelector();
	
	//create our HID interface.
	//This allows us to use controllers and joysticks to control
	//the robot. Data about the controllers is received using this interface.
	//The flashboard hid interface receives HID data from the flashboard.
	FlashboardHIDInterface hidInterface = new FlashboardHIDInterface();
	
	@Override
	protected void preInit(IterativeRobotInitializer initializer) {
		//set our mode selector
		initializer.modeSelector = modeSelector;
		
		//set our hid interface
		initializer.hidInterface = hidInterface;
		
		//in IterativeRobot we can insure that controllers are automatically updated. This insures
		//that button actions will work.
		initializer.autoUpdateHid = true;
	}
	@Override
	protected void robotInit() {
		
		//attach our hid interface to flashboard
		hidInterface.attachToFlashboard();
		//the hid interface needs to be updated periodically
		//so we add it as an update task to the Scheduler
		Scheduler.getInstance().addTask(hidInterface);
		
		//create an XboxController object. We define the connection of 
		//the object as 0. Depending on the HID interface used, this could mean 
		//different controllers. We would have to insure that this is the
		//right connection number with our used HID interface.
		xbox = new XboxController(0);
		
		//to access a value of an axis on the controller we can call:
		double value = xbox.getRawAxis(0);
		//this retrieves the axis value between -1 and 1 of a given axis numbered
		//from 0. The number of each axis can actually be found using the HID control
		//window of the Flashboard.
		
		//we can also check whether a button is down:
		boolean down = xbox.getRawButton(1);
		//the buttons are numbered from 1 and can also be checked using the Flashboard
		//HID control.
		
		//This way is not very intuitive though, so instead we can access control classes.
		//The xbox class contains several final variables which we can access to get data
		//from a specific axis or button easily.
		
		//An axis is defined by the Axis class.
		//A stick (2 axes: x, y) is defined in the Stick class.
		//A button is defined by the Button class.
		//A POV (Point Of View) is defined in the POV class.
		//A DPad (specific case of POV) is defined in the DPad class.
		
		//let's get the x-axis value of the controller's left stick:
		double x = xbox.LeftStick.AxisX.get();
		//we can also do it like this:
		x = xbox.LeftStick.getX();
		
		//we can check if button A is down too:
		down = xbox.A.get();
		
		//the DPad value can also be accessed and the current direction of pressed
		//can be checked. If not pressed, -1 is returned:
		int dir = xbox.DPad.get();
		
		//the xbox controller has 2 triggers: RT and LT. Those are in fact half-axes (0 - 1):
		double rt = xbox.RT.get();
		

		//The FlashLib HID package allows us to attach Action objects to 
		//HID buttons. Using this, we can start or stop actions depending on
		//the button status.
		//For the actions to be used, each button must be refreshed periodically. If the
		//button is part of a controller class, we can refresh all of it at once.
		//Thankfully, we can use the scheduler to refresh them. IterativeRobot's initializer in
		//preInit asks whether to add a task to the scheduler to update all controller (autoUpdateHid),
		//so we can set that to true and controllers will be updated automatically.
		//It is important to note that the task created by IterativeRobot updates controllers only if
		//isOperatorController() returns true.
		
		//we will create anonymous InstantAction objects which will print data to the
		//standard output just so we can see it working.
		
		//let's use the xbox's A button. We will create
		//an InstantAction which will print out data when A is
		//pressed.
		xbox.A.whenPressed(new InstantAction(){
			@Override
			protected void execute() {
				System.out.println("A: PRESSED");
			}
		});
		
		//Now, let's attach an action which will notify us when it is started and stopped.
		//We will run it while button B is held:
		xbox.B.whileHeld(new Action(){
			@Override
			protected void initialize() {
				System.out.println("B: INITIALIZE");
			}
			@Override
			protected void execute() {}
			@Override
			protected void end() {
				System.out.println("B: END");
			}
		});
		
		//If the button B is released, meaning it was held and was not
		//release, we will print indication of it:
		//Note that release is only called if the button was held, not pressed.
		xbox.B.whenReleased(new InstantAction(){
			@Override
			protected void execute() {
				System.out.println("B: RELEASED");
			}
		});
		
		//The xbox controller has a 4-button DPAD, FlashLib provides a button attachment
		//for each of those too:
		xbox.DPad.Up.whenPressed(new InstantAction(){
			@Override
			protected void execute() {
				System.out.println("DPAD UP: PRESSED");
			}
		});
		
		//We can also define actions for the DPAD as a whole. Meaning that pressing the DPAD,
		//no matter where can be considered like pressing a button:
		xbox.DPad.POV.whenPressed(new InstantAction(){
			@Override
			protected void execute() {
				System.out.println("DPAD: PRESSED");
			}
		});
		
		
		//sometimes we want to combine 2 buttons and activate actions only when 
		//both meet the requirements. we can implement something manually, or use
		//FlashLib's DoubleButton:
		doubleButton = new DoubleHIDButton(xbox.LB, xbox.RB);
		
		//This button object will be considered down only if both LB and RB buttons
		//are down. We can use it like any other button:
		doubleButton.whenPressed(new InstantAction(){
			@Override
			protected void execute() {
				System.out.println("LB && RB: PRESSED");
			}
		});
		
		//But! because this button is not part of a controller, IterativeRobot
		//will not know to refresh it. So we will have to manually create a task
		//for the scheduler to do. The scheduler can run Runnable objects, we
		//will create one that updates the button only if isOperatorControl is true:
		Scheduler.getInstance().addTask(()->{
			if(isOperatorControl())
				doubleButton.run();
		});
		
		
		//When our robot is in MODE_OPERATION, controllers will be updated and we
		//can check our buttons and axes.
		
		//setting ourselves to operation mode so we could checkout the 
		//values of controllers. This is not recommended when using an
		//actual movable robot since we are forcing it to stay in operation
		//and not allowing it to enter disabled mode at any given time.
		modeSelector.setMode(MODE_OPERATION);
	}

	@Override
	public boolean isOperatorControl() {
		/*
		 * This method indicates that our robot is controlled manually, by a user
		 * will controllers. By default it returns false, but it is necessary for it to
		 * return when our robot is controlled by user. If we don't define when we use
		 * controllers, background operations might not update them and they won't work.
		 */
		return isMode(MODE_OPERATION);
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
	}
}
