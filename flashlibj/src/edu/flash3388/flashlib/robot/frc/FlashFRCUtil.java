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
	 * 
	 * @param flashboardInitData initialization data for flashboard, or null to no init
	 */
	public static void initFlashLib(FlashboardInitData flashboardInitData){
		initFlashLib(RobotFactory.createFRCImplementation(), flashboardInitData);
	}
	/**
	 * Initializes FlashLib for FRC. Sets the parent directory for logs to "/home/lvuser", creates 
	 * a {@link Robot} implementation for FRC robots and uses given {@link FlashboardInitData} to initialize
	 * {@link Flashboard}.
	 * 
	 * @param robot the {@link Robot} implementation to initialize with
	 * @param flashboardInitData initialization data for flashboard, or null to no init
	 */
	public static void initFlashLib(Robot robot, FlashboardInitData flashboardInitData){
		Log.setParentDirectory("/home/lvuser");
		FlashRobotUtil.initFlashLib(robot, flashboardInitData);
		FlashUtil.getLog().addListener(new DriverStationLogListener());
		pdp = new PDP();
	}
}
