package edu.flash3388.flashlib.robot.modes;

import edu.flash3388.flashlib.util.beans.Property;

public class ManualRobotModeSupplier implements RobotModeSupplier, Property<RobotMode> {

	private RobotMode mCurrentMode;

	public ManualRobotModeSupplier() {
		mCurrentMode = RobotMode.DISABLED;
	}

	@Override
	public RobotMode getMode() {
		return getValue();
	}

	@Override
	public void setValue(RobotMode mode) {
		mCurrentMode = mode;
	}

	@Override
	public RobotMode getValue() {
		return mCurrentMode;
	}
}
