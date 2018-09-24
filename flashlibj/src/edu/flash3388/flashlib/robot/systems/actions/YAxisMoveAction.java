package edu.flash3388.flashlib.robot.systems.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.YAxisMovableInterface;

import java.util.function.DoubleSupplier;

public class YAxisMoveAction extends Action{

	private YAxisMovableInterface mInterface;
	private DoubleSupplier mSpeedSource;
	
	public YAxisMoveAction(YAxisMovableInterface motionInterface, DoubleSupplier speedSource) {
		this.mInterface = motionInterface;
		this.mSpeedSource = speedSource;
		
		if(motionInterface instanceof Subsystem) {
			requires((Subsystem) motionInterface);
		}
	}
	
	@Override
	protected void execute() {
		mInterface.moveY(mSpeedSource.getAsDouble());
	}

	@Override
	protected void end() {
		mInterface.stop();
	}
}
