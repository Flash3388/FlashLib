package edu.flash3388.flashlib.robot.rio;

import static edu.flash3388.flashlib.robot.FlashRoboUtil.*;

import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.Log;

/**
 * Class for specific RoboRio utilities.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class FlashRioUtil {
	
	private FlashRioUtil(){}
	
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
	 * Initializes FlashLib for RoboRio. Sets the parent directory for logs to "/home/lvuser".
	 * @param mode the init code for FlashLib.
	 */
	public static void initFlashLib(int mode){
		Log.setParentDirectory("/home/lvuser");
		FlashRoboUtil.initFlashLib(mode, RobotFactory.ImplType.RIO);
		pdp = new PDP();
	}
	/**
	 * Initializes FlashLib for RoboRio. Sets the parent directory for logs to "/home/lvuser".
	 * Initializes all FlashLib features.
	 */
	public static void initFlashLib(){
		initFlashLib(FLASHBOARD_INIT | SCHEDULER_INIT);
	}
}
