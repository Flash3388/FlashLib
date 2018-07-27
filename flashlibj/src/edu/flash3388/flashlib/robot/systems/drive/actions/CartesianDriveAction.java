package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.HolonomicDriveInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class CartesianDriveAction extends Action{
	
	private HolonomicDriveInterface mDriveInterface;
	private DoubleSource mYAxisSource;
	private DoubleSource mXAxisSource;
	private DoubleSource mRotateSource;
	
	public CartesianDriveAction(HolonomicDriveInterface driveInterface, DoubleSource y, DoubleSource x, DoubleSource rotate) {
		this.mDriveInterface = driveInterface;
		this.mXAxisSource = x;
		this.mYAxisSource = y;
		this.mRotateSource = rotate;
		
		if(driveInterface instanceof Subsystem) {
			requires((Subsystem) driveInterface);
		}
	}
	
	@Override
	protected void execute() {
		mDriveInterface.holonomicCartesian(mYAxisSource.get(), mXAxisSource.get(), mRotateSource.get());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
