package examples.robot.flashboard;

import edu.flash3388.flashlib.flashboard.FlashboardSlider;
import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.FlashboardInput;
import edu.flash3388.flashlib.flashboard.FlashboardLabel;
import edu.flash3388.flashlib.robot.IterativeRobot;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.StringProperty;

/*
 * In this example we will review usage of Flashboard with robots. We will
 * use IterativeRobot here, but the principles remain the same for all robot
 * bases.
 */
public class ExampleFlashboard extends IterativeRobot{

	/*
	 * We set up properties for flashboard controls. Those will allow us to control the data
	 * which set on the dashboard and get the data changed by the dashboard.
	 * 
	 * We need to use the FlashLib's beans package for that. The ConstantsHandler is not
	 * a must, just can be comfortable to use.
	 */
	
	//A double property example, we will use it for a slider control
	DoubleProperty sliderProperty = ConstantsHandler.addNumber("slider", 0.0);
	//A boolean property example, we will use it for a boolean label control
	BooleanProperty labelProperty = ConstantsHandler.addBoolean("label", false);
	//A string property example, we will use it for a string input control
	StringProperty inputProperty = ConstantsHandler.addString("input", "Something");
	
	@Override
	protected void preInit(IterativeRobotInitializer initializer) {
		/*
		 * In initialization, we have control over initialization parameters
		 * of Flashboard as well. This is available for all robot bases since it is 
		 * set in RobotBase.
		 */
		
		/*
		 * To control initialization of Flashboard we can set the `initFlashboard`
		 * variable. If it is true, flashboard will be initialized. If it is false,
		 * flashboard would not be initialized. If not initialized, we cannot use flashboard
		 * control.
		 * By default flashboard is not initialized
		 */
		//initializer.initFlashboard = true;
		
		/*
		 * To control flashboard initialization parameters, we can use a FlashboardInitData
		 * class which is provided by the initializer. flashboardInitData contains an instance
		 * of FlashboardInitData class by default. If we set the variable to null, flashboard
		 * will not be initialized.
		 */
		
		
		/*
		 * initMode sets the initialization mode for the Flashboard control. This allows configuration
		 * of which communication control will be initialized. 
		 * There are 3 options:
		 * 
		 * Flashboard.INIT_FULL - full initialization of flashboard control. Includes both camera server communications
		 * and standard communications.
		 * 
		 * Flashboard.INIT_CAM - initialization of flashboard camera server only. Will not provide sending 
		 * of standard controls to the flashboard, only camera data.
		 * 
		 * Flashboard.INIT_COMM - Initialization of flashboard standard communication only. Will not provide
		 * sending camera data to the flashboard, only standard controls.
		 * 
		 * 
		 * It is possible to separate flashboard control between 2 different softwares. Just initialize
		 * the flashboard control differently for each and make sure the flashboard software remote data
		 * is set to the correct host names for each part: camera server and control communications.
		 */
		//initializer.flashboardInitData.initMode = initModeForFlashboardControl;
		
		/*
		 * camPort sets the communications port to use for the Flashboard cam server.
		 * The default value is 5802. If this value is changed, it is necessary to update
		 * the flashboard software as well.
		 */
		//initializer.flashboardInitData.camPort = portForCameraServer;
		
		/*
		 * commPort sets the port to use for the flashboard standard communications. 
		 * The default value is 5800. If this value is changed, it is necessary to update the
		 * flashboard software as well.
		 */
		//initializer.flashboardInitData.commPort = portForCommunications;
		
		/*
		 * tcp sets whether the standard flashboard communications should use a TCP protocol
		 * or UDP protocol for data transfer.
		 * By default flashboard uses TCP. If this is changed, the flashboard software needs
		 * to be updated as well.
		 */
		//initializer.flashboardInitData.tcp = useTCPProtocol;
	}
	
	@Override
	protected void robotInit() {
		/*
		 * In robot init we should set up our Flashboard controls. In this example we will
		 * check out standard controls only, not Vision or Camera data.
		 * 
		 * Note that if flashboard standard communications control was not initialized,
		 * the bellow code will throw an exception.
		 */
		
		/*
		 * Lets add a slider control to the Flashboard. To use a slider control and both set and get 
		 * the from it, we need a DoubleProperty object. Using this we can set and get data from the slider
		 * control easily. 
		 * Other than the value property (which we created at the begining of the class), we
		 * have a name for the slider (will be shown above it), a minimum value, a maximum value
		 * and tick count. Tick count simply sets the amount of ticks between the minimum and
		 * maximum values. This indicates the standard value change.
		 * 
		 * Creating the slider returns a slider object for us. We can use it to change settings, but
		 * this is generally not needed.
		 */
		FlashboardSlider slider = Flashboard.putSlider("Slider Example", sliderProperty, 0.0, 1.0, 10);
		
		/*
		 * To get the slider value, we will call the get method from the slider value property.
		 * To set the slider value, we will call the set method from the slider value property.
		 * 
		 * The slider is updated automatically if the get method returns a new value. Which means
		 * it is checked periodically.
		 */
		double sliderValue = sliderProperty.get();
		sliderProperty.set(1.0);
		
		/*
		 * Now lets try an input field for boolean values. 
		 * There are 3 types of input fields:
		 * - boolean
		 * - string
		 * - double
		 * 
		 * Depending on the type used, Flashboard will make sure the input data is valid.
		 * 
		 * We will try a string one. For an input field, we need a property (StringProperty for boolean),
		 * which we already created. This will allow us to set and get the input field data. 
		 * In addition to a property, we can set the control name which will be shown above it.
		 * 
		 * This returns the control class used. Still not a necessity to use.
		 */
		FlashboardInput input = Flashboard.putInputField("Input Example", inputProperty);
		
		/*
		 * We can set the value or get the value using the property
		 * 
		 * The input field is updated automatically if the get method returns a new value. Which means
		 * it is checked periodically.
		 */
		inputProperty.set("Star Wars!");
		String inputValue = inputProperty.get();
		
		
		/*
		 * Another possible control is the label control. It shows a read-only data on
		 * the dashboard.
		 * There are 3 types:
		 * - boolean
		 * - string
		 * - double
		 * 
		 * We will use a boolean now.
		 * Because this control is a read-only and users cannot input data from the flashboard, 
		 * it doesn't require a BooleanProperty, but rather a BooleanSource which is a getter 
		 * interface and BooleanProperty extends it. 
		 * Again there is a name which will be shown next to the value.
		 * 
		 * This will return the control class which is still not a must for control.
		 */
		FlashboardLabel label = Flashboard.putLabel("Label Example", labelProperty);
		
		/*
		 * The label is updated if the get method of the data source returns a different value.
		 * Because we have a BooleanProperty, we can change that value by calling set.
		 */
		labelProperty.set(true);
		
		
		/*
		 * After we set up our controls, we can use them and access data from the properties at will.
		 * Usually, we would need to call Flashboard.start() at the end of the flashboard control creation
		 * to actually start the standard communications. But IterativeRobot and SimpleRobot bases
		 * already handle that.
		 */
	}

	/*
	 * Now we can use the properties anywhere in the robot software!
	 */
	
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
