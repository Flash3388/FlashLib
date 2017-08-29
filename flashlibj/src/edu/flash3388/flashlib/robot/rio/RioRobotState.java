package edu.flash3388.flashlib.robot.rio;

import edu.flash3388.flashlib.robot.RobotState;
import edu.wpi.first.wpilibj.DriverStation;

public class RioRobotState extends RobotState{

	private DriverStation ds = DriverStation.getInstance();
	
	@Override
	public boolean isDisabled() {
		return ds.isDisabled();
	}
	@Override
	public boolean isTeleop() {
		return ds.isOperatorControl();
	}
}
