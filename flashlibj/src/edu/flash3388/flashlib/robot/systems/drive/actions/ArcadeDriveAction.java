package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.TankDriveInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class ArcadeDriveAction extends Action{
	
	private TankDriveInterface mDriveInterface;
	private DoubleSource mMoveAxis;
	private DoubleSource mRotateAxis;
	
	public ArcadeDriveAction(TankDriveInterface driveInterface, DoubleSource move, DoubleSource rotate) {
		this.mDriveInterface = driveInterface;
		this.mRotateAxis = rotate;
		this.mMoveAxis = move;
		
		if(driveInterface instanceof Subsystem) {
			requires((Subsystem) driveInterface);
		}
	}
	
	@Override
	protected void execute() {
		mDriveInterface.arcadeDrive(mMoveAxis.get(), mRotateAxis.get());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}

}
