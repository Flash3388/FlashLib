package examples.robot;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashboardHIDInterface;
import edu.flash3388.flashlib.robot.FlashboardModeSelector;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.robot.actions.OmniDriveAction;
import edu.flash3388.flashlib.robot.io.devices.FlashSpeedController;
import edu.flash3388.flashlib.robot.io.devices.Talon;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.robot.systems.FlashDrive;

/*
 * In this class we will review quick creation of a robot using
 * one file. We will use pre-made system code for our systems and
 * simply create instances for them.
 * 
 * Our robot has an Omni-directional drive system with 4 motors
 * (one right, one left, one front, one rear). The right and left motors
 * move our robot in the Y axis (forward and backward) and front and back motors
 * move our robot in the X axis (right and left). We can combine both when wanted.
 */
public class ExampleQuickRobot extends IterativeRobot{
	
	private static final int MOTOR_RIGHT = 3, 
						 	MOTOR_LEFT = 2,
						 	MOTOR_FRONT = 1, 
						 	MOTOR_REAR = 0;
	
	//we will use a single custom operation mode which will 
	//indicate robot operation.
	public static final int MODE_OPERATION = 1;
	
	//create our mode selector. 
	//this mode selector is a window in Flashboard. 
	//You can easily select you operation modes from there.
	//But! you must define those modes for the window.
	FlashboardModeSelector modeSelector = new FlashboardModeSelector();
	//create our HID interface.
	//This allows us to use controllers and joysticks to control
	//the robot. Data about the controllers is received using this interface.
	//The flashboard hid interface receives HID data from the flashboard.
	FlashboardHIDInterface hidInterface = new FlashboardHIDInterface();
	
	//FlashDrive is a class for pre-made drive systems. It provides several
	//algorithms and is pretty cool to use.
	FlashDrive driveTrain;
	
	//we will use an xbox controller to control our drive system
	XboxController xbox;
	
	@Override
	protected void preInit(IterativeRobotInitializer initializer) {
		//since we use HAL PWM ports for controlling our drive system, we need to allow
		//initialization of the FlashLib HAL.
		//By allowing HAL initialization the IOFactory implementation is set to an HAL provider.
		initializer.initHAL = true;
		
		//indicate that we want to initialize flashboard
		initializer.initFlashboard = true;
		//since we have no use in the flashboard's camera server, we disable it
		initializer.flashboardInitData.initMode = Flashboard.INIT_COMM;
		
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
		
		//the hid interface needs to be updated periodically
		//so we add it as an update task to the Scheduler
		Scheduler.getInstance().addTask(hidInterface);
		
		//create our xbox controller. It should be connected to channel 0 of
		//the hid interface
		xbox = new XboxController(0);
		
		//We need to create our drive system here. For that we will
		//initialize speed controllers and than create.
		//We will use the Talon speed controller class.
		//Because we initialized HAL in preInit, the IOFactory class will
		//now provide us with HAL ports. Since HAL was initialized, IOFactory
		//will provide ports from HAL, so we can just pass the port numbers to our controllers
		//and it will use IOFactory to create the PWM ports.
		
		FlashSpeedController motorRight = new Talon(MOTOR_RIGHT);
		FlashSpeedController motorLeft = new Talon(MOTOR_LEFT);
		FlashSpeedController motorFront = new Talon(MOTOR_FRONT);
		FlashSpeedController motorRear = new Talon(MOTOR_REAR);
		
		//creating an instance of FlashDrive with our speed controllers
		driveTrain = new FlashDrive(motorRight, motorLeft, motorFront, motorRear);
		
		//creating a default action for the drive train.
		//we will use a built-in action which drives our system
		//by calling the omniDrive method.
		//We will pass it our driveTrain and the Axis objects of our controller's
		//right stick.
		Action stickDrive = new OmniDriveAction(driveTrain, 
				xbox.RightStick.AxisY, xbox.RightStick.AxisX);
		//adding a dependency of the action on our drive system
		stickDrive.requires(driveTrain);
		
		//we set the action as default action for our drive system.
		//so whenever there is no action running on the system, the scheduler will start
		//this action. (Will not happen in disabled mode since actions do not run during
		//that mode)
		driveTrain.setDefaultAction(stickDrive);
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
