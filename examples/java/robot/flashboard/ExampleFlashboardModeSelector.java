package examples.robot.flashboard;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.FlashboardModeSelector;
import edu.flash3388.flashlib.robot.IterativeRobot;

/*
 * In this example we will be looking at the Flashboard mode selector and
 * how to use it.
 */
public class ExampleFlashboardModeSelector extends IterativeRobot{

	//we will use a single custom operation mode which will 
	//indicate robot operation.
	public static final int MODE_OPERATION = 1;
	
	//create our mode selector. 
	//this mode selector is a window in Flashboard. 
	//You can easily select you operation modes from there.
	//But! you must define those modes for the window.
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
		//there is no need to attach the mode selector to flashboard since it i s attached
		//automatically when IterativeRobot calls Flashboard.start()
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
