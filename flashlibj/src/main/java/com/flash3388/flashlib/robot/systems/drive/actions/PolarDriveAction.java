package com.flash3388.flashlib.robot.systems.drive.actions;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.systems.drive.HolonomicDrive;

import java.util.function.DoubleSupplier;

public class PolarDriveAction extends Action {
	
	private final HolonomicDrive mDriveInterface;
	private final DoubleSupplier mMagnitudeSource;
	private final DoubleSupplier mDirectionSource;
	private final DoubleSupplier mRotationSource;
	
	public PolarDriveAction(HolonomicDrive driveInterface, DoubleSupplier magnitude, DoubleSupplier direction, DoubleSupplier rotate) {
		this.mDriveInterface = driveInterface;
		this.mDirectionSource = direction;
		this.mMagnitudeSource = magnitude;
		this.mRotationSource = rotate;
	}
	
	@Override
	protected void execute() {
		mDriveInterface.holonomicPolar(mMagnitudeSource.getAsDouble(), mDirectionSource.getAsDouble(), mRotationSource.getAsDouble());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
