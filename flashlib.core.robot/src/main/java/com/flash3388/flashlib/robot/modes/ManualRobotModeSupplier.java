package com.flash3388.flashlib.robot.modes;

import com.beans.Property;

import java.util.concurrent.atomic.AtomicReference;

public class ManualRobotModeSupplier implements RobotModeSupplier, Property<RobotMode> {

	private final AtomicReference<RobotMode> mCurrentMode;

	public ManualRobotModeSupplier() {
		mCurrentMode = new AtomicReference<>(RobotMode.DISABLED);
	}

	@Override
	public RobotMode get() {
		return mCurrentMode.get();
	}

	@Override
	public void set(RobotMode mode) {
		mCurrentMode.set(mode);
	}
}
