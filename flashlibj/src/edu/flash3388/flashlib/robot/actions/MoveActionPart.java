package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.SourceAction;
import edu.flash3388.flashlib.util.beans.DoubleSource;

public class MoveActionPart extends SourceAction{

	private DoubleSource speedSource;
	
	public MoveActionPart(DoubleSource speed){
		this.speedSource = speed;
	}

	@Override
	protected void execute() {
		dataSource.set(speedSource.get());
	}
	@Override
	protected void end() {
	}
}
