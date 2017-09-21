package examples.robot.fullRobot1;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.sbc.FlashboardModeSelector;
import edu.flash3388.flashlib.robot.sbc.IterativeRobot;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.DoubleProperty;

/*
 * The main class of our robot. It extends IterativeRobot.
 */
public class Robot extends IterativeRobot{

	//we will use a single custom operation mode which will 
	//indicate robot operation.
	public static final int MODE_OPERATION = 1;
	
	//our drive system 
	DriveSystem driveTrain;
	
	//action for tank drive motion
	Action tankDriveAction;
	//action for arcade drive motion
	Action arcadeDriveAction;
	
	//speed property for motion with the actions: arcade - move source, tank - right source
	DoubleProperty speedSource_move_right = ConstantsHandler.addNumber("move/right", 0.0);
	//speed property for motion with the actions: arcade - rotate source, tank - left source
	DoubleProperty speedSource_rotate_left = ConstantsHandler.addNumber("rotate/left", 0.0);
	
	//create our mode selector. 
	//we will use a flashboard mode selector which is basically a combo box
	//on the flashboard.
	FlashboardModeSelector modeSelector = new FlashboardModeSelector();
	
	@Override
	protected void preInit(IterativeRobotInitializer initializer) {
		//indicate that we want to initialize flashboard
		initializer.initFlashboard = true;
		//since we have no use in the flashboard's camera server, we disable it
		initializer.flashboardInitData.initMode = Flashboard.INIT_COMM;
		
		//set our mode selector
		initializer.modeSelector = modeSelector;
	}
	@Override
	protected void robotInit() {
		//initializing our drive system
		driveTrain = new DriveSystem();
		
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
		
		//define the operation modes to our mode selector.
		modeSelector.addOption("Operation", MODE_OPERATION);
		//attach our mode selector to flashboard
		modeSelector.attachToFlashboard();
		
		//add a button to the flashboard which will start the arcade drive action
		Flashboard.putButton("Arcade Drive", arcadeDriveAction);
		//add a button to the flashboard which will start the tank drive action
		Flashboard.putButton("Tank Drive", tankDriveAction);
		
		//put a slider on flashboard which will control the move/right speed property
		Flashboard.putSlider("move/right", speedSource_move_right, -1.0, 1.0, 20);
		//put a slider on flashboard which will control the rotate/left speed property
		Flashboard.putSlider("move/left", speedSource_rotate_left, -1.0, 1.0, 20);
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
