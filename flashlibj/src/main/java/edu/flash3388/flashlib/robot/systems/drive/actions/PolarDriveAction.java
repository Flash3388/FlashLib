package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.HolonomicDriveInterface;

import java.util.function.DoubleSupplier;

public class PolarDriveAction extends Action{
	
	private final HolonomicDriveInterface mDriveInterface;
	private final DoubleSupplier mMagnitudeSource;
	private final DoubleSupplier mDirectionSource;
	private final DoubleSupplier mRotationSource;
	
	public PolarDriveAction(HolonomicDriveInterface driveInterface, DoubleSupplier magnitude, DoubleSupplier direction, DoubleSupplier rotate) {
		this.mDriveInterface = driveInterface;
		this.mDirectionSource = direction;
		this.mMagnitudeSource = magnitude;
		this.mRotationSource = rotate;
		
		if(driveInterface instanceof Subsystem) {
			requires((Subsystem) driveInterface);
		}
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
