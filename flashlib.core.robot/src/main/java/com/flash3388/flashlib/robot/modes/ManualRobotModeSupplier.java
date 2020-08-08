package com.flash3388.flashlib.robot.modes;

import com.beans.Property;

public class ManualRobotModeSupplier implements Property<RobotMode> {

	private RobotMode mCurrentMode;

	public ManualRobotModeSupplier() {
		mCurrentMode = RobotMode.DISABLED;
	}

	@Override
	public RobotMode get() {
		return mCurrentMode;
	}

	@Override
	public void set(RobotMode mode) {
		mCurrentMode = mode;
	}
}
