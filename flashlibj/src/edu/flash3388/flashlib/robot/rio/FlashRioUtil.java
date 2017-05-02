package edu.flash3388.flashlib.robot.rio;

import static edu.flash3388.flashlib.robot.FlashRoboUtil.*;

import edu.flash3388.flashlib.robot.FlashRoboUtil;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.util.Log;

public class FlashRioUtil {
	
	private FlashRioUtil(){}
	
	private static PDP pdp;

	public static PDP getPDP(){
		return pdp;
	}
	
	//--------------------------------------------------------------------
	//--------------------------Init--------------------------------------
	//--------------------------------------------------------------------
	
	public static void initFlashLib(int mode){
		Log.setParentDirectory("/home/lvuser");
		FlashRoboUtil.initFlashLib(mode, RobotFactory.ImplType.RIO);
		pdp = new PDP();
	}
	public static void initFlashLib(){
		initFlashLib(FLASHBOARD_INIT | SCHEDULER_INIT);
	}
}
