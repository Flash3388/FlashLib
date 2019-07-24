package com.flash3388.flashlib.robot.systems.drive.actions;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSpeed;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class TankDriveAction extends Action {
	
	private final TankDrive mDriveInterface;
	private final Supplier<TankDriveSpeed> mSpeedSupplier;

    public TankDriveAction(TankDrive driveInterface, Supplier<TankDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;
    }

	public TankDriveAction(TankDrive driveInterface, DoubleSupplier right, DoubleSupplier left) {
		this(driveInterface, ()->new TankDriveSpeed(right.getAsDouble(), left.getAsDouble()));
	}
	
	@Override
	protected void execute() {
		mDriveInterface.tankDrive(mSpeedSupplier.get());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
