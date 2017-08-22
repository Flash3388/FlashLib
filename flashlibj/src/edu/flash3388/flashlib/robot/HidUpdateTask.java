package edu.flash3388.flashlib.robot;

/**
 * A runnable implementation which calls {@link FlashRoboUtil#updateHID()} if {@link RobotState#isRobotTeleop()}
 * returns true.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class HidUpdateTask implements Runnable{
	
	@Override
	public void run() {
		if(RobotState.isRobotTeleop())
			FlashRoboUtil.updateHID();
	}
}
