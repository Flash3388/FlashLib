package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.HolonomicDriveInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class OmniDriveAction extends Action{
	
	private HolonomicDriveInterface mDriveInterface;
	private DoubleSource mYAxisSource;
	private DoubleSource mXAxisSource;
	
	public OmniDriveAction(HolonomicDriveInterface driveInterface, DoubleSource y, DoubleSource x) {
		this.mDriveInterface = driveInterface;
		this.mXAxisSource = x;
		this.mYAxisSource = y;
		
		if(driveInterface instanceof Subsystem) {
			requires((Subsystem) driveInterface);
		}
	}
	
	@Override
	protected void execute() {
		mDriveInterface.omniDrive(mYAxisSource.get(), mXAxisSource.get());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
