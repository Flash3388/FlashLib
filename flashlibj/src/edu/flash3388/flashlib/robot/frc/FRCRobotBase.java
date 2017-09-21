package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.ModeSelector;
import edu.flash3388.flashlib.robot.Robot;
import edu.wpi.first.wpilibj.SampleRobot;

public class FRCRobotBase extends SampleRobot implements Robot{
	
	private ModeSelector selector = new FRCModeSelector();

	@Override
	public ModeSelector getModeSelector() {
		return selector;
	}
	@Override
	public boolean isFRC() {
		return true;
	}
}
