package examples.robot.frc;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.robot.frc.IterativeFRCRobot;
import edu.flash3388.flashlib.util.ConstantsHandler;
import edu.flash3388.flashlib.util.beans.DoubleProperty;

import edu.wpi.first.wpilibj.Ultrasonic;

/*
 * A simple code showcasing use of flashboard controls.
 * Shows the ultasonic data on the flashboard. Uses input from flashboard to change a filter for our
 * drive system.
 */
public class ExampleFlashboard extends IterativeFRCRobot{
	
	Ultrasonic sonic1;
	Ultrasonic sonic2;
	
	DoubleProperty filterSpeed = ConstantsHandler.putNumber("filterSpeed", 1.0);
	
	@Override
	protected void preInit(RobotInitializer initializer) {
		/*
		 * Sets the flashboard to use UDP protocol and not TCP (which is the default). You must make sure
		 * the flashboard software is set to use UDP or they won't be able to connect to each other.
		 */
		//initializer.flashboardInitData.tcp = false;
		
		/*
		 * Sets the initialization mode of the Flashboard control. There are 2 initialization parameters:
		 * camera server and communications. Camera server initialization initializes the Flashboard camera server, 
		 * allowing users to send data to the Flashboard. Communications initialization initializes the Flashboard
		 * standard data communications, allowing users to show and receive information from the Flashboard including
		 * vision data.
		 * 
		 * The init mode is an integer holding initialization bits. Each bit represents whether to initialize
		 * a certain feature. 
		 * For only camera server, use Flashboard.INIT_CAM.
		 * For only communications, use Flashboard.INIT_COMM.
		 * For both, use Flashboard.INIT_FULL.
		 * 
		 * By default, the initCode is Flashboard.INIT_FULL
		 */
		//initializer.flashboardInitData.initMode = Flashboard.INIT_COMM;
		
		/*
		 * Sets the local communications server port. If the initialization mode when Flashboard initializes
		 * does not initialize the communications server, this will have no effect. The default port
		 * is described by Flashboard.PORT_ROBOT. It is necessary to make sure the flashboard software
		 * is also updated to this port in its configuration, otherwise connection will not be successful.
		 */
		//initializer.flashboardInitData.commPort = port;
		/*
		 * Sets the local camera server port. If the initialization mode when Flashboard initializes
		 * does not initialize the camera server, this will have no effect. The default port
		 * is described by Flashboard.CAMERA_PORT_ROBOT. It is necessary to make sure the flashboard software
		 * is also updated to this port in its configuration, otherwise connection will not be successful.
		 */
		//initializer.flashboardInitData.camPort = port;
		
		/*
		 * Sets the flashboard initialization data to null. Cancels the initialization of flashboard
		 */
		//initializer.flashboardInitData = null;
	}
	@Override
	protected void initRobot() {
		/*
		 * Creates ultrasonic sensors from WPILib 
		 */
		sonic1 = new Ultrasonic(0, 1);
		sonic2 = new Ultrasonic(2, 3);
		/*
		 * Sets those sensors to automatic update mode.
		 */
		sonic1.setAutomaticMode(true);
		
		/*
		 * Creates and attaches flashboard double properties. Creates double data sources pointing to the ultrasonics.
		 */
		Flashboard.putData("sonic1", ()->sonic1.getRangeMM());
		Flashboard.putData("sonic2", ()->sonic2.getRangeMM());
		
		/*
		 * Creates a slider infput from flashboard to update our filterSpeed. It updates our property directly.
		 * The slider as a minimum value of 0.0, a maximum value of 1.0 and the default change of values is
		 * 0.1 ((1.0 - 0.0) / 10).
		 */
		Flashboard.putSlider("Some Slider", filterSpeed, 0.0, 1.0, 10);
		
		/*
		 * Starts the flashboard
		 */
		Flashboard.start();
	}
	
	@Override
	protected void teleopInit() {
	}
	@Override
	protected void teleopPeriodic() {
	}

	@Override
	protected void autonomousInit() {}
	@Override
	protected void autonomousPeriodic() {}

	@Override
	protected void disabledInit() {
	}
	@Override
	protected void disabledPeriodic() {
	}
}
