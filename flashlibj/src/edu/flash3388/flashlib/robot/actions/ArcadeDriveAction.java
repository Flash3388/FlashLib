package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class ArcadeDriveAction extends Action{
	
	private TankDriveSystem driveTrain;
	private DoubleSource rotateAxis, moveAxis;
	
	public ArcadeDriveAction(TankDriveSystem driveTrain, DoubleSource move, DoubleSource rotate) {
		this.driveTrain = driveTrain;
		this.rotateAxis = rotate;
		this.moveAxis = move;
		
		if(driveTrain instanceof Subsystem)
			requires((Subsystem)driveTrain);
	}
	
	@Override
	protected void execute() {
		driveTrain.arcadeDrive(moveAxis.get(), rotateAxis.get());
	}
	@Override
	protected void end() {
		driveTrain.stop();
	}

}