package edu.flash3388.flashlib.robot.sbc;

import edu.flash3388.flashlib.robot.HIDInterface;
import edu.flash3388.flashlib.robot.Robot;
import edu.flash3388.flashlib.robot.Scheduler;

public class SBCRobot implements Robot{
	
	private Scheduler scheduler = new Scheduler();
	private HIDInterface hid = new SBCHidInterface();

	@Override
	public Scheduler scheduler() {
		return scheduler;
	}

	@Override
	public HIDInterface hid() {
		return hid;
	}

	@Override
	public boolean isDisabled() {
		return SBCRobotBase.stateSelector().getState() == StateSelector.STATE_DISABLED;
	}
	@Override
	public boolean isOperatorControl() {
		return SBCRobotBase.stateSelector().getState() == StateSelector.STATE_TELEOP;
	}

	@Override
	public boolean isFRC() {
		return false;
	}
}
