package edu.flash3388.flashlib.robot.systems.actions;

import edu.flash3388.flashlib.robot.scheduling.Action;
import edu.flash3388.flashlib.robot.scheduling.Subsystem;
import edu.flash3388.flashlib.robot.systems.XAxisMovableInterface;

import java.util.function.DoubleSupplier;

public class XAxisMoveAction extends Action{

	private XAxisMovableInterface mInterface;
	private DoubleSupplier mSpeedSource;
	
	public XAxisMoveAction(XAxisMovableInterface motionInterface, DoubleSupplier speedSource) {
		this.mInterface = motionInterface;
		this.mSpeedSource = speedSource;
		
		if(motionInterface instanceof Subsystem) {
			requires((Subsystem) motionInterface);
		}
	}
	
	@Override
	protected void execute() {
		mInterface.moveX(mSpeedSource.getAsDouble());
	}

	@Override
	protected void end() {
		mInterface.stop();
	}
}
