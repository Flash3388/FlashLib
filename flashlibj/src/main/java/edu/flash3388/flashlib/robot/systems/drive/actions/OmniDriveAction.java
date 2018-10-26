package edu.flash3388.flashlib.robot.systems.drive.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.drive.HolonomicDriveInterface;

import java.util.function.DoubleSupplier;

public class OmniDriveAction extends Action{
	
	private final HolonomicDriveInterface mDriveInterface;
	private final DoubleSupplier mYAxisSource;
	private final DoubleSupplier mXAxisSource;
	
	public OmniDriveAction(HolonomicDriveInterface driveInterface, DoubleSupplier y, DoubleSupplier x) {
		this.mDriveInterface = driveInterface;
		this.mXAxisSource = x;
		this.mYAxisSource = y;
		
		if(driveInterface instanceof Subsystem) {
			requires((Subsystem) driveInterface);
		}
	}
	
	@Override
	protected void execute() {
		mDriveInterface.omniDrive(mYAxisSource.getAsDouble(), mXAxisSource.getAsDouble());
	}

	@Override
	protected void end() {
		mDriveInterface.stop();
	}
}
