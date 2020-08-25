package com.flash3388.flashlib.robot.systems.drive.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;
import com.flash3388.flashlib.robot.systems.drive.TankDriveSpeed;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class TankDriveAction extends ActionBase {
	
	private final TankDrive mDriveInterface;
	private final Supplier<? extends TankDriveSpeed> mSpeedSupplier;

    public TankDriveAction(TankDrive driveInterface, Supplier<? extends TankDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;
    }

    public TankDriveAction(TankDrive driveInterface, TankDriveSpeed driveSpeed) {
        this(driveInterface, Suppliers.of(driveSpeed));
    }

	public TankDriveAction(TankDrive driveInterface, DoubleSupplier right, DoubleSupplier left) {
		this(driveInterface, ()->new TankDriveSpeed(right.getAsDouble(), left.getAsDouble()));
	}

    public TankDriveAction(TankDrive driveInterface, double right, double left) {
        this(driveInterface, new TankDriveSpeed(right, left));
    }
	
	@Override
	public void execute() {
		mDriveInterface.tankDrive(mSpeedSupplier.get());
	}

	@Override
    public void end(boolean wasInterrupted) {
		mDriveInterface.stop();
	}
}
