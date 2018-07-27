package edu.flash3388.flashlib.robot.systems.actions.drive;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.TankDriveInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class ArcadeDriveAction extends Action{
	
	private TankDriveInterface driveTrain;
	private DoubleSource rotateAxis, moveAxis;
	
	public ArcadeDriveAction(TankDriveInterface driveTrain, DoubleSource move, DoubleSource rotate) {
		this.driveTrain = driveTrain;
		this.rotateAxis = rotate;
		this.moveAxis = move;
		
		if(driveTrain instanceof Subsystem) {
			requires((Subsystem) driveTrain);
		}
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
