package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.TankDriveInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class TankDriveAction extends Action{
	
	private TankDriveInterface mDriveInterface;
	private DoubleSource mRightSource;
	private DoubleSource mLeftSource;
	
	public TankDriveAction(TankDriveInterface driveInterface, DoubleSource right, DoubleSource left) {
		this.mDriveInterface = driveInterface;
		this.mRightSource = right;
		this.mLeftSource = left;
		
		if(driveInterface instanceof Subsystem) {
			requires((Subsystem) driveInterface);
		}
	}
	
	@Override
	protected void execute() {
		mDriveInterface.tankDrive(mRightSource.get(), mLeftSource.get());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
