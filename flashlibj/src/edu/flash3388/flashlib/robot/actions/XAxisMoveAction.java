package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.systems.XAxisMovable;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class XAxisMoveAction extends Action{

	private XAxisMovable xMovable;
	private DoubleSource speedSource;
	
	public XAxisMoveAction(XAxisMovable xMovable, DoubleSource speedSource) {
		this.xMovable = xMovable;
		this.speedSource = speedSource;
		
		if(xMovable instanceof Subsystem)
			requires((Subsystem)xMovable);
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
