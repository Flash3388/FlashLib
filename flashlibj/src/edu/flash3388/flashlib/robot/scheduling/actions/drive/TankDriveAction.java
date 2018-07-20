package edu.flash3388.flashlib.robot.scheduling.actions.drive;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class TankDriveAction extends Action{
	
	private TankDriveSystem driveTrain;
	private DoubleSource leftAxis, rightAxis;
	
	public TankDriveAction(TankDriveSystem driveTrain, DoubleSource right, DoubleSource left) {
		this.driveTrain = driveTrain;
		this.leftAxis = left;
		this.rightAxis = right;
		
		if(driveTrain instanceof Subsystem)
			requires((Subsystem)driveTrain);
	}
	
	@Override
	protected void execute() {
		driveTrain.tankDrive(rightAxis.get(), leftAxis.get());
	}
	@Override
	protected void end() {
		driveTrain.stop();
	}
}
