package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.Action;
import edu.flash3388.flashlib.robot.Subsystem;
import edu.flash3388.flashlib.robot.systems.YAxisMovable;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class YAxisMoveAction extends Action{

	private YAxisMovable yMovable;
	private DoubleSource speedSource;
	
	public YAxisMoveAction(YAxisMovable yMovable, DoubleSource speedSource) {
		this.yMovable = yMovable;
		this.speedSource = speedSource;
		
		if(yMovable instanceof Subsystem)
			requires((Subsystem)yMovable);
	}
	
	@Override
	protected void execute() {
		yMovable.moveY(speedSource.get());
	}
	@Override
	protected void end() {
		yMovable.stop();
	}
}