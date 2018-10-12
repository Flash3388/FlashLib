package edu.flash3388.flashlib.robot.modes;

public class ManualRobotModeSupplier implements RobotModeSupplier {

	private RobotMode mCurrentMode;

	public ManualRobotModeSupplier() {
		mCurrentMode = RobotMode.DISABLED;
	}

	@Override
	public RobotMode getMode() {
		return mCurrentMode;
	}

	public void setMode(RobotMode mode) {
		mCurrentMode = mode;
	}
}
