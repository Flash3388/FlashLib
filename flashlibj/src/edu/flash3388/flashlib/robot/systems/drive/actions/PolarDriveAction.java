package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.HolonomicDriveInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class PolarDriveAction extends Action{
	
	private HolonomicDriveInterface mDriveInterface;
	private DoubleSource mMagnitudeSource;
	private DoubleSource mDirectionSource;
	private DoubleSource mRotationSource;
	
	public PolarDriveAction(HolonomicDriveInterface driveInterface, DoubleSource magnitude, DoubleSource direction, DoubleSource rotate) {
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
		mDriveInterface.holonomicPolar(mMagnitudeSource.get(), mDirectionSource.get(), mRotationSource.get());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
