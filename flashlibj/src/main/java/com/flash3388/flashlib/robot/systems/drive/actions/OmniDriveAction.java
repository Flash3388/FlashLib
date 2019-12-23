package com.flash3388.flashlib.robot.systems.drive.actions;

import com.flash3388.flashlib.robot.scheduling.actions.Action;
import com.flash3388.flashlib.robot.systems.drive.OmniDrive;
import com.flash3388.flashlib.robot.systems.drive.OmniDriveSpeed;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class OmniDriveAction extends Action {
	
	private final OmniDrive mDriveInterface;
	private final Supplier<? extends OmniDriveSpeed> mSpeedSupplier;

    public OmniDriveAction(OmniDrive driveInterface, Supplier<? extends OmniDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;
    }

	public OmniDriveAction(OmniDrive driveInterface, DoubleSupplier y, DoubleSupplier x) {
		this(driveInterface, ()->new OmniDriveSpeed(y.getAsDouble(), x.getAsDouble()));
	}

    public OmniDriveAction(OmniDrive driveInterface, DoubleSupplier front, DoubleSupplier right, DoubleSupplier back, DoubleSupplier left) {
        this(driveInterface, ()->new OmniDriveSpeed(front.getAsDouble(), right.getAsDouble(), back.getAsDouble(), left.getAsDouble()));
    }
	
	@Override
	protected void execute() {
		mDriveInterface.omniDrive(mSpeedSupplier.get());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
