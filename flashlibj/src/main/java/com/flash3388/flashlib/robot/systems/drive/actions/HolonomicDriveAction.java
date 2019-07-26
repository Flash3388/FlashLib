package com.flash3388.flashlib.robot.systems.drive.actions;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.systems.drive.HolonomicDrive;
import com.flash3388.flashlib.robot.systems.drive.HolonomicDriveSpeed;
import com.jmath.vectors.Vector2;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public class HolonomicDriveAction extends Action {
	
	private final HolonomicDrive mDriveInterface;
	private final Supplier<HolonomicDriveSpeed> mSpeedSupplier;

    public HolonomicDriveAction(HolonomicDrive driveInterface, Supplier<HolonomicDriveSpeed> speedSupplier) {
        mDriveInterface = driveInterface;
        mSpeedSupplier = speedSupplier;
    }

    public static HolonomicDriveAction cartesian(HolonomicDrive driveInterface, DoubleSupplier y, DoubleSupplier x, DoubleSupplier rotate) {
        return new HolonomicDriveAction(driveInterface, ()->new HolonomicDriveSpeed(new Vector2(y.getAsDouble(), x.getAsDouble()), rotate.getAsDouble()));
    }

	public static HolonomicDriveAction polar(HolonomicDrive driveInterface, DoubleSupplier magnitude, DoubleSupplier direction, DoubleSupplier rotate) {
		return new HolonomicDriveAction(driveInterface, ()->new HolonomicDriveSpeed(Vector2.polar(magnitude.getAsDouble(), direction.getAsDouble()), rotate.getAsDouble()));
	}
	
	@Override
	protected void execute() {
		mDriveInterface.holonomicDrive(mSpeedSupplier.get());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}