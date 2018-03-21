package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.Flashboard.FlashboardInitData;
import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.RobotInterface;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.LogUtil;

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
	 * a {@link RobotInterface} implementation for FRC robots and uses given {@link FlashboardInitData} to initialize
	 * {@link Flashboard}.
	 * 
	 * @param flashboardInitData initialization data for flashboard, or null to no init
	 */
	public static void initFlashLib(FlashboardInitData flashboardInitData){
		initFlashLib(RobotFactory.createFRCImplementation(), flashboardInitData);
	}
	/**
	 * Initializes FlashLib for FRC. Sets the parent directory for logs to "/home/lvuser", creates 
	 * a {@link RobotInterface} implementation for FRC robots and uses given {@link FlashboardInitData} to initialize
	 * {@link Flashboard}.
	 * 
	 * @param robot the {@link RobotInterface} implementation to initialize with
	 * @param flashboardInitData initialization data for flashboard, or null to no init
	 */
	public static void initFlashLib(RobotInterface robot, FlashboardInitData flashboardInitData){
		LogUtil.setLogsParentDirectory("/home/lvuser");
		FlashRobotUtil.initFlashLib(robot, RobotFactory.createFRCHIDInterface(), flashboardInitData);
		FlashUtil.getLogger().addHandler(new DriverStationLogHandler());
		pdp = new PDP();
		
		if(Flashboard.flashboardInit())
			Flashboard.attach(pdp);
	}
}
