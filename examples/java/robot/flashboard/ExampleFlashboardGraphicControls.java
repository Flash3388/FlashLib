package examples.robot.flashboard;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.FlashboardBarChart;
import edu.flash3388.flashlib.flashboard.FlashboardBooleanIndicator;
import edu.flash3388.flashlib.flashboard.FlashboardDirectionIndicator;
import edu.flash3388.flashlib.flashboard.FlashboardXYChart;
import edu.flash3388.flashlib.robot.FlashboardHIDInterface;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.robot.ManualModeSelector;
import edu.flash3388.flashlib.robot.Scheduler;
import edu.flash3388.flashlib.robot.hid.XboxController;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleSource;

/*
 * In this example we will check out the graphic flashboard data controls.
 * We will create several graphs which will show us values from 
 * an xbox controller.
 */
public class ExampleFlashboardGraphicControls extends IterativeRobot{

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
		
		//create our xbox controller for channel 0 of the 
		//flashboard HID interface
		xbox = new XboxController(0);
		
		//When our robot is in MODE_OPERATION, controllers will be updated and we
		//can see our controls on the flashboard change.
		
		//setting ourselves to operation mode so we could checkout the 
		//values of controllers. This is not recommended when using an
		//actual movable robot since we are forcing it to stay in operation
		//and not allowing it to enter disabled mode at any given time.
		modeSelector.setMode(MODE_OPERATION);
		
		/*
		 * We will create the following controls:
		 * - Direction indicator: will show the value of the xbox's DPAD.
		 * - A Bar chart: will show axes data for left stick axes
		 * - Boolean indicator: will show whether button B is pressed
		 * - Line char: will show data about the value of the Right stick's Y axis as a function
		 * of time.
		 */
		
		//The FlashboardDirectionIndicator control shows a 360 circle and indicates the current
		//direction. This can allows us to show orientation or direction of something. It uses
		//a DoubleSource object from FlashLib's beans package.
		//Our source object will return the degrees of the DPAD (0->360). But if the value is -1, meaning
		//that it is not pressed, it will return 0.
		DoubleSource indicatorSource = ()->{
			int degrees = xbox.DPad.get();
			if(degrees < 0)
				return 0;
			return degrees;
		};
		FlashboardDirectionIndicator dirIndicator = 
				Flashboard.putDirectionIndicator("DPAD", indicatorSource);
		
		//The FlashboardBarChart controls shows a bar chart for multiple sources. Using
		//it we can show a value which is not a function. We will show values
		//for both our axes on the left stick.
		//A bar chart as a minimum and maximum values. For us it's -1.0 to 1.0, since
		//that's the range for axes.
		FlashboardBarChart barChart = Flashboard.putBarChart("LeftStick", -1.0, 1.0);
		
		//Now we need to add our "bars", each is an independent DoubleSource which can
		//show different data.
		barChart.addSeries("Y", xbox.LeftStick.AxisY);
		barChart.addSeries("X", xbox.LeftStick.AxisX);
		
		
		//The FlashboardBooleanIndicator controls shows a rectangle. If the current value is 
		//false, the rectangle is red, if it is true then the rectangle is green. We will
		//show the value of the B button. The control uses a BooleanSource to receive data.
		FlashboardBooleanIndicator boolIndicator = 
				Flashboard.putBooleanIndicator("B", xbox.B);
		
		
		//The FlashboardXYChart shows a function chart with 2 variables: x and y.
		//It can be used to show the change of values over time, or a relation between
		//2 values. 
		//There are 2 types of charts:
		//- Line: a simple chart with lines
		//- Area: marks the area between the function and the x-axis
		//We will use a line chart to show the change of axis Y from the right stick over time.
		//We will need 2 DoubleSource object: for time and for the Y axis. 
		//Other than that, we can define the range of Y values and of X values.
		//If the values are outside the defined ranges, those ranges will be moved.
		DoubleSource timeSource = ()->{
			return FlashUtil.secs();
		};
		FlashboardXYChart lineChart = Flashboard.putLineChart("Right Y", 
				timeSource, xbox.RightStick.AxisY, 0.0, 10.0, -1.0, 1.0);
		
		
		/*
		 * All our controls will be automaticallty updated with values received from the
		 * source objects, so we can just relax and watch.
		 */
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
