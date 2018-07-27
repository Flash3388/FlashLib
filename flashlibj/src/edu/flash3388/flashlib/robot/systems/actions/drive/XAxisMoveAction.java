package edu.flash3388.flashlib.robot.systems.actions.drive;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.XAxisDriveInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class XAxisMoveAction extends Action{

	private XAxisDriveInterface xMovable;
	private DoubleSource speedSource;
	
	public XAxisMoveAction(XAxisDriveInterface xMovable, DoubleSource speedSource) {
		this.xMovable = xMovable;
		this.speedSource = speedSource;
		
		if(xMovable instanceof Subsystem) {
			requires((Subsystem) xMovable);
		}
	}
	
	@Override
	protected void execute() {
		xMovable.moveX(speedSource.get());
	}

	@Override
	protected void end() {
		xMovable.stop();
	}
}
