package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.hid.Axis;
import edu.flash3388.flashlib.robot.systems.HolonomicDriveSystem;

public class HIDCartesianDriveAction extends Action{
	
	private HolonomicDriveSystem driveTrain;
	private Axis xAxis, yAxis, rotateAxis;
	
	public HIDCartesianDriveAction(HolonomicDriveSystem driveTrain, Axis y, Axis x, Axis rotate) {
		this.driveTrain = driveTrain;
		this.xAxis = x;
		this.yAxis = y;
		this.rotateAxis = rotate;
		
		if(driveTrain instanceof Subsystem)
			requires((Subsystem)driveTrain);
	}
	
	@Override
	protected void execute() {
		driveTrain.holonomicCartesian(yAxis.get(), xAxis.get(), rotateAxis.get());
	}
	@Override
	protected void end() {
		driveTrain.stop();
	}
}
