package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.TankDriveInterface;

import java.util.function.DoubleSupplier;

public class ArcadeDriveAction extends Action{
	
	private TankDriveInterface mDriveInterface;
	private DoubleSupplier mMoveAxis;
	private DoubleSupplier mRotateAxis;
	
	public ArcadeDriveAction(TankDriveInterface driveInterface, DoubleSupplier move, DoubleSupplier rotate) {
		this.mDriveInterface = driveInterface;
		this.mRotateAxis = rotate;
		this.mMoveAxis = move;
		
		if(driveInterface instanceof Subsystem) {
			requires((Subsystem) driveInterface);
		}
	}
	
	@Override
	protected void execute() {
		mDriveInterface.arcadeDrive(mMoveAxis.getAsDouble(), mRotateAxis.getAsDouble());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}

}
