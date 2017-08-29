package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.RobotState;

public class SbcRobotState extends RobotState{

	@Override
	public boolean isDisabled() {
		return SbcRobot.isDisabled();
	}
	@Override
	public boolean isTeleop() {
		return SbcRobot.getCurrentState() == StateSelector.STATE_TELEOP;
	}
}
