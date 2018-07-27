package edu.flash3388.flashlib.robot.frc.modes;

import edu.flash3388.flashlib.robot.modes.RobotMode;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;
import edu.wpi.first.wpilibj.DriverStation;

public class FRCRobotModeSupplier implements RobotModeSupplier {

	private final DriverStation mDs;

	public FRCRobotModeSupplier() {
		mDs = DriverStation.getInstance();
	}
	
	@Override
	public RobotMode getMode() {
		if (mDs.isOperatorControl()) {
			return FRCRobotMode.TELEOP;
		}
		if (mDs.isAutonomous()) {
			return FRCRobotMode.AUTONOMOUS;
		}
		if (mDs.isTest()) {
			return FRCRobotMode.TEST;
		}

		return FRCRobotMode.DISABLED;
	}
}
