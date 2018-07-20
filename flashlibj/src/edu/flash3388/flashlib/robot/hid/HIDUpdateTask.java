package edu.flash3388.flashlib.robot.hid;

import edu.flash3388.flashlib.robot.FlashRobotUtil;
import edu.flash3388.flashlib.robot.RobotFactory;
import edu.flash3388.flashlib.robot.RobotInterface;

/**
 * A runnable implementation which calls {@link FlashRobotUtil#updateHID()} if {@link RobotInterface#isOperatorControl()} from
 * the {@link RobotFactory} implementation returns true.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class HIDUpdateTask implements Runnable{
	
	@Override
	public void run() {
		if(RobotFactory.getImplementation().isOperatorControl())
			FlashRobotUtil.updateHID();
	}
}
