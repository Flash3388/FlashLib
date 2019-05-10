package com.flash3388.flashlib.robot.systems.drive.actions;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.systems.drive.HolonomicDrive;

import java.util.function.DoubleSupplier;

public class CartesianDriveAction extends Action {
	
	private final HolonomicDrive mDriveInterface;
	private final DoubleSupplier mYAxisSource;
	private final DoubleSupplier mXAxisSource;
	private final DoubleSupplier mRotateSource;
	
	public CartesianDriveAction(HolonomicDrive driveInterface, DoubleSupplier y, DoubleSupplier x, DoubleSupplier rotate) {
		this.mDriveInterface = driveInterface;
		this.mXAxisSource = x;
		this.mYAxisSource = y;
		this.mRotateSource = rotate;
	}
	
	@Override
	protected void execute() {
		mDriveInterface.holonomicCartesian(mYAxisSource.getAsDouble(), mXAxisSource.getAsDouble(), mRotateSource.getAsDouble());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
