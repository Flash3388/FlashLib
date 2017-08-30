package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.Log;

/**
 * Class for specific FRC robot utilities.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashFRCUtil {
	
	private FlashFRCUtil(){}
	
	private static PDP pdp;

	/**
	 * Gets the power distribution panel instance used to track power data.
	 * @return the pdp instance
	 */
	public static PDP getPDP(){
		return pdp;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Init--------------------------------------
	//--------------------------------------------------------------------
	
	/**
	 * Initializes FlashLib for FRC. Sets the parent directory for logs to "/home/lvuser", creates 
	 * a {@link Robot} implementation for FRC robots and uses given {@link FlashboardInitData} to initialize
	 * {@link Flashboard}.
	 */
	public static void initFlashLib(FlashboardInitData flashboardInitData){
		Log.setParentDirectory("/home/lvuser");
		Robot robot = RobotFactory.createFRCImplementation();
		FlashRobotUtil.initFlashLib(robot, flashboardInitData);
		FlashUtil.getLog().addLoggingInterface(new FRCLoggingInterface());
		pdp = new PDP();
	}
}
