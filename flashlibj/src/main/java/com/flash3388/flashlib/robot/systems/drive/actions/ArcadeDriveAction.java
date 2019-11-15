package com.flash3388.flashlib.robot.systems.drive.actions;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.systems.drive.ArcadeDriveSpeed;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class ArcadeDriveAction extends Action {
	
	private final TankDrive mDriveInterface;
	private final Supplier<ArcadeDriveSpeed> mSpeedSupplier;

    public ArcadeDriveAction(TankDrive driveInterface, Supplier<ArcadeDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;
    }

	public ArcadeDriveAction(TankDrive driveInterface, DoubleSupplier move, DoubleSupplier rotate) {
		this(driveInterface, ()->new ArcadeDriveSpeed(move.getAsDouble(), rotate.getAsDouble()));
	}
	
	@Override
	protected void execute() {
		mDriveInterface.arcadeDrive(mSpeedSupplier.get());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}

}
