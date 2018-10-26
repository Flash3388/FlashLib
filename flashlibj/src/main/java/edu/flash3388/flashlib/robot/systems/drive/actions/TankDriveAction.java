package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.TankDriveInterface;

import java.util.function.DoubleSupplier;

public class TankDriveAction extends Action{
	
	private final TankDriveInterface mDriveInterface;
	private final DoubleSupplier mRightSource;
	private final DoubleSupplier mLeftSource;
	
	public TankDriveAction(TankDriveInterface driveInterface, DoubleSupplier right, DoubleSupplier left) {
		this.mDriveInterface = driveInterface;
		this.mRightSource = right;
		this.mLeftSource = left;
		
		if(driveInterface instanceof Subsystem) {
			requires((Subsystem) driveInterface);
		}
	}
	
	@Override
	protected void execute() {
		mDriveInterface.tankDrive(mRightSource.getAsDouble(), mLeftSource.getAsDouble());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
