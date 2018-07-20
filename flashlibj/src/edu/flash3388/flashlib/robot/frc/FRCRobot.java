package edu.flash3388.flashlib.robot.frc;

import edu.flash3388.flashlib.robot.modes.ModeSelector;
import edu.flash3388.flashlib.robot.RobotInterface;

import edu.wpi.first.wpilibj.DriverStation;

public class FRCRobot implements RobotInterface{
	
	private DriverStation ds = DriverStation.getInstance();
	private ModeSelector selector = new FRCModeSelector();

	@Override
	public ModeSelector getModeSelector() {
		return selector;
	}
	
	@Override
	public boolean isDisabled() {
		return ds.isDisabled();
	}
	@Override
	public boolean isOperatorControl() {
		return ds.isOperatorControl();
	}
	
	@Override
	public boolean isFRC() {
		return true;
	}
}
