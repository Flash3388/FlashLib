package edu.flash3388.flashlib.robot.scheduling.actions.combined;

import edu.flash3388.flashlib.robot.scheduling.SourceAction;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class MoveActionPart extends SourceAction{

	private DoubleSource speedSource;
	
	public MoveActionPart(DoubleSource speed){
		this.speedSource = speed;
	}

	@Override
	protected void execute() {
	}
	@Override
	protected void end() {
	}

	@Override
	public double get() {
		return speedSource.get();
	}
}
