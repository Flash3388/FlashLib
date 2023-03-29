package com.flash3388.flashlib.robot.modes;

import com.beans.Property;
import com.flash3388.flashlib.util.FlashLibMainThread;

public class ManualRobotModeSupplier implements RobotModeSupplier, Property<RobotMode> {

	private final FlashLibMainThread mMainThread;
	private RobotMode mCurrentMode;

	public ManualRobotModeSupplier(FlashLibMainThread mainThread) {
		mMainThread = mainThread;
		mCurrentMode = RobotMode.DISABLED;
	}

	@Override
	public RobotMode get() {
		mMainThread.verifyCurrentThread();
		return mCurrentMode;
	}

	@Override
	public void set(RobotMode mode) {
		mMainThread.verifyCurrentThread();
		mCurrentMode = mode;
	}
}
