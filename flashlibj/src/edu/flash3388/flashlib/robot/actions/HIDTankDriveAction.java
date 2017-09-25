package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.hid.Axis;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;

public class HIDTankDriveAction extends Action{
	
	private TankDriveSystem driveTrain;
	private Axis leftAxis, rightAxis;
	
	public HIDTankDriveAction(TankDriveSystem driveTrain, Axis right, Axis left) {
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
