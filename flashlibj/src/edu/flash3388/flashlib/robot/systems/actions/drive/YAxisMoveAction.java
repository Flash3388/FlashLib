package edu.flash3388.flashlib.robot.systems.actions.drive;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.YAxisDriveInterface;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class YAxisMoveAction extends Action{

	private YAxisDriveInterface yMovable;
	private DoubleSource speedSource;
	
	public YAxisMoveAction(YAxisDriveInterface yMovable, DoubleSource speedSource) {
		this.yMovable = yMovable;
		this.speedSource = speedSource;
		
		if(yMovable instanceof Subsystem) {
			requires((Subsystem) yMovable);
		}
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
