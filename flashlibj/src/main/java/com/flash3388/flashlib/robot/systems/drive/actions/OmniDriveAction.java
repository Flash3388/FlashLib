package com.flash3388.flashlib.robot.systems.drive.actions;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.systems.drive.OmniDrive;

import java.util.function.DoubleSupplier;

public class OmniDriveAction extends Action {
	
	private final OmniDrive mDriveInterface;
	private final DoubleSupplier mYAxisSource;
	private final DoubleSupplier mXAxisSource;
	
	public OmniDriveAction(OmniDrive driveInterface, DoubleSupplier y, DoubleSupplier x) {
		this.mDriveInterface = driveInterface;
		this.mXAxisSource = x;
		this.mYAxisSource = y;
	}
	
	@Override
	protected void execute() {
		mDriveInterface.omniDrive(mYAxisSource.getAsDouble(), mXAxisSource.getAsDouble());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
