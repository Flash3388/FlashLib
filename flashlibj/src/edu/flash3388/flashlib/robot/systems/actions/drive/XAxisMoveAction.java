package edu.flash3388.flashlib.robot.systems.actions.drive;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.XAxisMovableInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class XAxisMoveAction extends Action{

	private XAxisMovableInterface mInterface;
	private DoubleSource mSpeedSource;
	
	public XAxisMoveAction(XAxisMovableInterface motionInterface, DoubleSource speedSource) {
		this.mInterface = motionInterface;
		this.mSpeedSource = speedSource;
		
		if(motionInterface instanceof Subsystem) {
			requires((Subsystem) motionInterface);
		}
	}
	
	@Override
	protected void execute() {
		mInterface.moveX(mSpeedSource.get());
	}

	@Override
	protected void end() {
		mInterface.stop();
	}
}
