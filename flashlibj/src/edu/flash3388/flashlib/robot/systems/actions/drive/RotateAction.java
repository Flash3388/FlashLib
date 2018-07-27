package edu.flash3388.flashlib.robot.systems.actions.drive;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.Rotatable;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class RotateAction extends Action{

	private Rotatable mRotatable;
	private DoubleSource mSpeedSource;
	
	public RotateAction(Rotatable rotatable, DoubleSource speedSource) {
		this.mRotatable = rotatable;
		this.mSpeedSource = speedSource;
		
		if(rotatable instanceof Subsystem) {
			requires((Subsystem) rotatable);
		}
	}
	
	@Override
	protected void execute() {
		mRotatable.rotate(mSpeedSource.get());
	}

	@Override
	protected void end() {
		mRotatable.stop();
	}
}
