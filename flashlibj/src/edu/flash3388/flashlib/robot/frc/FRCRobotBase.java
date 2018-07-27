package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.frc.hid.FRCHIDInterface;
import edu.flash3388.flashlib.robot.frc.modes.FRCModeSelector;
import edu.flash3388.flashlib.robot.hid.HIDInterface;
import edu.flash3388.flashlib.robot.modes.ModeSelector;
import edu.flash3388.flashlib.robot.RobotInterface;
import edu.wpi.first.wpilibj.SampleRobot;

public abstract class FRCRobotBase extends SampleRobot implements RobotInterface {
	
	private final ModeSelector mModeSelector = new FRCModeSelector();
	private final HIDInterface mHidInterface = new FRCHIDInterface();

	@Override
	public ModeSelector getModeSelector() {
		return mModeSelector;
	}

	@Override
	public HIDInterface getHidInterface() {
		return mHidInterface;
	}
}
