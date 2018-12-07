package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.HolonomicDrive;

import java.util.function.DoubleSupplier;

public class CartesianDriveAction extends Action{
	
	private final HolonomicDrive mDriveInterface;
	private final DoubleSupplier mYAxisSource;
	private final DoubleSupplier mXAxisSource;
	private final DoubleSupplier mRotateSource;
	
	public CartesianDriveAction(HolonomicDrive driveInterface, DoubleSupplier y, DoubleSupplier x, DoubleSupplier rotate) {
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
		mDriveInterface.holonomicCartesian(mYAxisSource.getAsDouble(), mXAxisSource.getAsDouble(), mRotateSource.getAsDouble());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
