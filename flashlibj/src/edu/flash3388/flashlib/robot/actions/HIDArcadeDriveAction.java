package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.hid.Axis;
import edu.flash3388.flashlib.robot.systems.TankDriveSystem;

public class HIDArcadeDriveAction extends Action{
	
	private TankDriveSystem driveTrain;
	private Axis rotateAxis, moveAxis;
	
	public HIDArcadeDriveAction(TankDriveSystem driveTrain, Axis move, Axis rotate) {
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
