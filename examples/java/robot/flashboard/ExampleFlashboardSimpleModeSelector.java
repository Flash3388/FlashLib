package examples.robot.flashboard;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.FlashboardSimpleModeSelector;
import edu.flash3388.flashlib.robot.IterativeRobot;

/*
 * In this example we will check out the FlashboardSimpleModeSelector for
 * selecting operation modes from the flashboard.
 */
public class ExampleFlashboardSimpleModeSelector extends IterativeRobot{

	//we will use a single custom operation mode which will 
	//indicate robot operation.
	public static final int MODE_OPERATION = 1;
	
	//create our mode selector. 
	//we will use a flashboard mode selector which is basically a combo box
	//on the flashboard.
	FlashboardSimpleModeSelector modeSelector = new FlashboardSimpleModeSelector();

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
		//define the operation modes to our mode selector.
		modeSelector.addOption("Operation", MODE_OPERATION);
		//attach our mode selector to flashboard
		modeSelector.attachToFlashboard();
	}

	@Override
	protected void disabledInit() {
		//let's print the we entered disabled mode so we could see that the mode selector
		//works
		System.out.println("DISABLED");
	}
	@Override
	protected void disabledPeriodic() {
	}

	@Override
	protected void modeInit(int mode) {
		//let's print the we entered a mode so we could see that the mode selector
		//works
		System.out.println("MODE: "+mode);
	}
	@Override
	protected void modePeriodic(int mode) {
	}
}
