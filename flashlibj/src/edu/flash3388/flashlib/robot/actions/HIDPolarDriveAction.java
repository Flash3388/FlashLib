package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.hid.Axis;
import edu.flash3388.flashlib.robot.systems.HolonomicDriveSystem;

public class HIDPolarDriveAction extends Action{
	
	private HolonomicDriveSystem driveTrain;
	private Axis dirAxis, magAxis, rotateAxis;
	
	public HIDPolarDriveAction(HolonomicDriveSystem driveTrain, Axis magnitude, Axis direction, Axis rotate) {
		this.driveTrain = driveTrain;
		this.dirAxis = direction;
		this.magAxis = magnitude;
		this.rotateAxis = rotate;
		
		if(driveTrain instanceof Subsystem)
			requires((Subsystem)driveTrain);
	}
	
	@Override
	protected void execute() {
		driveTrain.holonomicPolar(magAxis.get(), dirAxis.get(), rotateAxis.get());
	}
	@Override
	protected void end() {
		driveTrain.stop();
	}
}
