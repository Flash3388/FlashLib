package edu.flash3388.flashlib.robot.scheduling.actions.drive;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.HolonomicDriveSystem;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class OmniDriveAction extends Action{
	
	private HolonomicDriveSystem driveTrain;
	private DoubleSource xAxis, yAxis;
	
	public OmniDriveAction(HolonomicDriveSystem driveTrain, DoubleSource y, DoubleSource x) {
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
