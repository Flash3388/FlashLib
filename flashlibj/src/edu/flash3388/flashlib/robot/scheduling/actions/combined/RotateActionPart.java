package edu.flash3388.flashlib.robot.scheduling.actions.combined;

import edu.flash3388.flashlib.robot.scheduling.SourceAction;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class RotateActionPart extends SourceAction{

	private DoubleSource speedSource;
	
	public RotateActionPart(DoubleSource speed){
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
