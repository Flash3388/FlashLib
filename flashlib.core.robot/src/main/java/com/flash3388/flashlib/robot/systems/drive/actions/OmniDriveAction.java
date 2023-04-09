package com.flash3388.flashlib.robot.systems.drive.actions;

import com.beans.util.function.Suppliers;
import com.flash3388.flashlib.robot.systems.drive.OmniDrive;
import com.flash3388.flashlib.robot.systems.drive.OmniDriveSpeed;
import com.flash3388.flashlib.scheduling.ActionControl;
import com.flash3388.flashlib.scheduling.FinishReason;
import com.flash3388.flashlib.scheduling.actions.ActionBase;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class OmniDriveAction extends ActionBase {
	
	private final OmniDrive mDriveInterface;
	private final Supplier<? extends OmniDriveSpeed> mSpeedSupplier;

    public OmniDriveAction(OmniDrive driveInterface, Supplier<? extends OmniDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;

        requires(driveInterface);
    }

    public OmniDriveAction(OmniDrive driveInterface, OmniDriveSpeed driveSpeed) {
        this(driveInterface, Suppliers.of(driveSpeed));
    }

	public OmniDriveAction(OmniDrive driveInterface, DoubleSupplier y, DoubleSupplier x) {
		this(driveInterface, ()->new OmniDriveSpeed(y.getAsDouble(), x.getAsDouble()));
	}

    public OmniDriveAction(OmniDrive driveInterface, double y, double x) {
        this(driveInterface, new OmniDriveSpeed(y, x));
    }

    public OmniDriveAction(OmniDrive driveInterface, DoubleSupplier front, DoubleSupplier right, DoubleSupplier back, DoubleSupplier left) {
        this(driveInterface, ()->new OmniDriveSpeed(front.getAsDouble(), right.getAsDouble(), back.getAsDouble(), left.getAsDouble()));
    }

    public OmniDriveAction(OmniDrive driveInterface, double front, double right, double back, double left) {
        this(driveInterface, ()->new OmniDriveSpeed(front, right, back, left));
    }
	
	@Override
	public void execute(ActionControl control) {
		mDriveInterface.omniDrive(mSpeedSupplier.get());
	}

	@Override
    public void end(FinishReason reason) {
		mDriveInterface.stop();
	}
}
