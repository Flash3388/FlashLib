package com.flash3388.flashlib.robot.systems.drive.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.systems.drive.ArcadeDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ArcadeDriveAction extends ActionBase {
	
	private final TankDrive mDriveInterface;
	private final Supplier<? extends ArcadeDriveSpeed> mSpeedSupplier;

    public ArcadeDriveAction(TankDrive driveInterface, Supplier<? extends ArcadeDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;

        requires(driveInterface);
    }

	public ArcadeDriveAction(TankDrive driveInterface, DoubleSupplier move, DoubleSupplier rotate) {
		this(driveInterface, ()->new ArcadeDriveSpeed(move.getAsDouble(), rotate.getAsDouble()));
	}

    public ArcadeDriveAction(TankDrive driveInterface, ArcadeDriveSpeed driveSpeed) {
        this(driveInterface, Suppliers.of(driveSpeed));
    }

    public ArcadeDriveAction(TankDrive driveInterface, double move, double rotate) {
        this(driveInterface, new ArcadeDriveSpeed(move, rotate));
    }
	
	@Override
	public void execute() {
		mDriveInterface.arcadeDrive(mSpeedSupplier.get());
	}

	@Override
	public void end(boolean wasInterrupted) {
		mDriveInterface.stop();
	}

}
