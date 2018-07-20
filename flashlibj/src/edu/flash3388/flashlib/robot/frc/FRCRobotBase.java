package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.modes.ModeSelector;
import edu.flash3388.flashlib.robot.RobotInterface;
import edu.wpi.first.wpilibj.SampleRobot;

public class FRCRobotBase extends SampleRobot implements RobotInterface{
	
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
