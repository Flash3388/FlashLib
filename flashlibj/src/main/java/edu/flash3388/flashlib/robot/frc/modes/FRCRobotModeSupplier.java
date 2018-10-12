package edu.flash3388.flashlib.robot.frc.modes;

import edu.flash3388.flashlib.robot.modes.RobotMode;
import edu.flash3388.flashlib.robot.modes.RobotModeSupplier;
import edu.wpi.first.wpilibj.DriverStation;

public class FRCRobotModeSupplier implements RobotModeSupplier {

	private final DriverStation mDriverStation;

	public FRCRobotModeSupplier(DriverStation driverStation) {
		mDriverStation = driverStation;
	}
	
	@Override
	public RobotMode getMode() {
		if (mDriverStation.isOperatorControl()) {
			return FRCRobotMode.OPERATOR_CONTROL;
		}
		if (mDriverStation.isAutonomous()) {
			return FRCRobotMode.AUTONOMOUS;
		}
		if (mDriverStation.isTest()) {
			return FRCRobotMode.TEST;
		}

		return FRCRobotMode.DISABLED;
	}
}
