package edu.flash3388.flashlib.robot.scheduling.actions.drive;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.HolonomicDriveSystem;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class PolarDriveAction extends Action{
	
	private HolonomicDriveSystem driveTrain;
	private DoubleSource dirAxis, magAxis, rotateAxis;
	
	public PolarDriveAction(HolonomicDriveSystem driveTrain, 
			DoubleSource magnitude, DoubleSource direction, DoubleSource rotate) {
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
