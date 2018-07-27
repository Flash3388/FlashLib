package edu.flash3388.flashlib.robot.systems.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.YAxisMovableInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class YAxisMoveAction extends Action{

	private YAxisMovableInterface mInterface;
	private DoubleSource mSpeedSource;
	
	public YAxisMoveAction(YAxisMovableInterface motionInterface, DoubleSource speedSource) {
		this.mInterface = motionInterface;
		this.mSpeedSource = speedSource;
		
		if(motionInterface instanceof Subsystem) {
			requires((Subsystem) motionInterface);
		}
	}
	
	@Override
	protected void execute() {
		mInterface.moveY(mSpeedSource.get());
	}

	@Override
	protected void end() {
		mInterface.stop();
	}
}
