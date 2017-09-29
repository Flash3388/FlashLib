package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.systems.HolonomicDriveSystem;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class CartesianDriveAction extends Action{
	
	private HolonomicDriveSystem driveTrain;
	private DoubleSource xAxis, yAxis, rotateAxis;
	
	public CartesianDriveAction(HolonomicDriveSystem driveTrain,
			DoubleSource y, DoubleSource x, DoubleSource rotate) {
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
