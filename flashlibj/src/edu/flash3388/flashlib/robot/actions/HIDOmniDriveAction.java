package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.hid.Axis;
import edu.flash3388.flashlib.robot.systems.HolonomicDriveSystem;

public class HIDOmniDriveAction extends Action{
	
	private HolonomicDriveSystem driveTrain;
	private Axis xAxis, yAxis;
	
	public HIDOmniDriveAction(HolonomicDriveSystem driveTrain, Axis y, Axis x) {
		this.driveTrain = driveTrain;
		this.xAxis = x;
		this.yAxis = y;
		
		if(driveTrain instanceof Subsystem)
			requires((Subsystem)driveTrain);
	}
	
	@Override
	protected void execute() {
		driveTrain.omniDrive(yAxis.get(), xAxis.get());
	}
	@Override
	protected void end() {
		driveTrain.stop();
	}
}
