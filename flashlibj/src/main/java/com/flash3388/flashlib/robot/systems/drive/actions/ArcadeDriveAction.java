package com.flash3388.flashlib.robot.systems.drive.actions;

import com.flash3388.flashlib.robot.scheduling.Action;
import com.flash3388.flashlib.robot.scheduling.Subsystem;
import com.flash3388.flashlib.robot.systems.drive.TankDrive;

import java.util.function.DoubleSupplier;

public class ArcadeDriveAction extends Action {
	
	private final TankDrive mDriveInterface;
	private final DoubleSupplier mMoveAxis;
	private final DoubleSupplier mRotateAxis;
	
	public ArcadeDriveAction(TankDrive driveInterface, DoubleSupplier move, DoubleSupplier rotate) {
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
