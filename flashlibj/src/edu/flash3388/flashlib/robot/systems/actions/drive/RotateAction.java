package edu.flash3388.flashlib.robot.systems.actions.drive;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.Rotatable;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class RotateAction extends Action{

	private Rotatable rotatable;
	private DoubleSource speedSource;
	
	public RotateAction(Rotatable rotatable, DoubleSource speedSource) {
		this.rotatable = rotatable;
		this.speedSource = speedSource;
		
		if(rotatable instanceof Subsystem) {
			requires((Subsystem) rotatable);
		}
	}
	
	@Override
	protected void execute() {
		rotatable.rotate(speedSource.get());
	}

	@Override
	protected void end() {
		rotatable.stop();
	}
}
