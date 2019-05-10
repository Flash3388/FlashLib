package com.flash3388.flashlib.robot.systems.drive.actions;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;

import java.util.function.DoubleSupplier;

public class TankDriveAction extends Action {
	
	private final TankDrive mDriveInterface;
	private final DoubleSupplier mRightSource;
	private final DoubleSupplier mLeftSource;
	
	public TankDriveAction(TankDrive driveInterface, DoubleSupplier right, DoubleSupplier left) {
		this.mDriveInterface = driveInterface;
		this.mRightSource = right;
		this.mLeftSource = left;
	}
	
	@Override
	protected void execute() {
		mDriveInterface.tankDrive(mRightSource.getAsDouble(), mLeftSource.getAsDouble());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
