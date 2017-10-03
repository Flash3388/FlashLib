package examples.robot.fullrobot;

import edu.flash3388.flashlib.flashboard.FlashboardChooser;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.FlashboardHIDInterface;
import edu.flash3388.flashlib.robot.FlashboardSimpleModeSelector;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.Property;

/*
 * The main class of our robot. It extends IterativeRobot.
 */
public class Robot extends IterativeRobot{

	//constant for the manual operation mode
	public static final int OPERATION_MODE_MANUAL = 0x1;
	//constant for the automatic operation mode
	public static final int OPERATION_MODE_AUTO = 0x2;
	
	//our drive system 
	DriveSystem driveTrain;
	
	//we will use an xbox controller to control our drive system
	XboxController xbox;
	
	//action for tank drive motion
	Action tankDriveAction;
	//action for arcade drive motion
	Action arcadeDriveAction;
	//action for manual control 
	Action stickDriveAction;
	
	//speed property for motion with the actions: arcade - move source, tank - right source
	DoubleProperty speedSource_move_right = ConstantsHandler.addNumber("move/right", 0.0);
	//speed property for motion with the actions: arcade - rotate source, tank - left source
	DoubleProperty speedSource_rotate_left = ConstantsHandler.addNumber("rotate/left", 0.0);
	//property for the action which we will run during auto mode
	Property<Action> autoActionProp;
	
	//create our mode selector. 
	//we will use a flashboard mode selector which is basically a combo box
	//on the flashboard.
	FlashboardSimpleModeSelector modeSelector = new FlashboardSimpleModeSelector();
	//create our HID interface.
	//This allows us to use controllers and joysticks to control
	//the robot. Data about the controllers is received using this interface.
	//The flashboard hid interface receives HID data from the flashboard.
	FlashboardHIDInterface hidInterface = new FlashboardHIDInterface();
	
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
		
		//since we use HAL PWM ports for controlling our drive system, we need to allow
		//initialization of the FlashLib HAL
		initializer.initHAL = true;
		
		//in IterativeRobot we can insure that controllers are automatically updated. This insures
		//that button actions will work.
		initializer.autoUpdateHid = true;
	}
	@Override
	protected void robotInit() {
		//initializing our drive system
		driveTrain = new DriveSystem();
		
		//initializing our xbox controller for channel 0 of the HID interface
		xbox = new XboxController(0);
		
		//creating our tank drive action. passing our drive system as parameter and the two 
		//speed sources which will detect the motion speed.
		tankDriveAction = new TankDriveAction(driveTrain, speedSource_move_right, speedSource_rotate_left);
		//indicate to the scheduler that the action uses our drive system. this is very
		//important. usually it should be done from within the action class, but for this example
		//we will do it here.
		tankDriveAction.requires(driveTrain);
		
		//creating our arcade drive action. passing our drive system as parameter and the two 
		//speed sources which will detect the motion speed.
		arcadeDriveAction = new ArcadeDriveAction(driveTrain, speedSource_move_right, speedSource_rotate_left);
		//indicate to the scheduler that the action uses our drive system. this is very
		//important. usually it should be done from within the action class, but for this example
		//we will do it here.
		arcadeDriveAction.requires(driveTrain);
		
		//creating our stick drive action. passing our drive system as parameter and two axes 
		//from our xbox controller which will control the movement of the system.
		stickDriveAction = new StickTankDriveAction(driveTrain, 
				xbox.RightStick.AxisY, xbox.LeftStick.AxisX);
		//indicate to the scheduler that the action uses our drive system. this is very
		//important. usually it should be done from within the action class, but for this example
		//we will do it here.
		stickDriveAction.requires(driveTrain);
		//we set the stick drive as default action for our drive system.
		//so whenever there is no action running on the system, the scheduler will start
		//this action. (Will not happen in disabled mode since actions do not run during
		//that mode)
		driveTrain.setDefaultAction(stickDriveAction);
		
		//define the operation modes to our mode selector.
		modeSelector.addOption("Manual", OPERATION_MODE_MANUAL);
		modeSelector.addOption("Auto", OPERATION_MODE_AUTO);
		//attach our mode selector to flashboard
		modeSelector.attachToFlashboard();
		
		//attach our hid interface to flashboard
		hidInterface.attachToFlashboard();
		//the hid interface needs to be updated periodically
		//so we add it as an update task to the Scheduler
		Scheduler.getInstance().addTask(hidInterface);
		
		//put a slider on flashboard which will control the move/right speed property
		Flashboard.putSlider("move/right", speedSource_move_right, -1.0, 1.0, 20);
		//put a slider on flashboard which will control the rotate/left speed property
		Flashboard.putSlider("rotate/left", speedSource_rotate_left, -1.0, 1.0, 20);
		
		//put a combo box on the flashboard and add several options to it. 
		//this combo box will be used to select which action to execute during
		//auto mode.
		FlashboardChooser<Action> autoChooser = Flashboard.<Action>putChooser("Auto Chooser");
		autoChooser.addDefault("Empty", Action.EMPTY);
		autoChooser.addOption("Arcade Drive", arcadeDriveAction);
		autoChooser.addOption("Tank Drive", tankDriveAction);
		
		//saving the selected value property of the dashboard chooser
		autoActionProp = autoChooser.selectedValueProperty();
	}

	@Override
	public boolean isOperatorControl() {
		/*
		 * This method indicates that our robot is controlled manually, by a user
		 * will controllers. By default it returns false, but it is necessary for it to
		 * return when our robot is controlled by user. This data will be used
		 * in background operations.
		 * In our robot we have 3 modes: disabled, manual and auto. Manual mode is
		 * controlled by an operator 
		 */
		return isMode(OPERATION_MODE_MANUAL);
	}
	
	@Override
	protected void disabledInit() {
	}
	@Override
	protected void disabledPeriodic() {
	}

	@Override
	protected void modeInit(int mode) {
		switch(mode){
			case OPERATION_MODE_MANUAL:
				manualInit();
				break;
			case OPERATION_MODE_AUTO:
				autoInit();
				break;
		}
	}
	@Override
	protected void modePeriodic(int mode) {
		switch(mode){
			case OPERATION_MODE_MANUAL:
				manualPeriodic();
				break;
			case OPERATION_MODE_AUTO:
				autoPeriodic();
				break;
		}
	}
	
	private void manualInit(){
		
	}
	private void manualPeriodic(){
	}
	
	private void autoInit(){
		//Here we retrieve the action to execute during auto mode from the dashboard chooser and
		//start it.
		Action autoAction = autoActionProp.getValue();
		if(autoAction != null)
			autoAction.start();
	}
	private void autoPeriodic(){
	}
}
