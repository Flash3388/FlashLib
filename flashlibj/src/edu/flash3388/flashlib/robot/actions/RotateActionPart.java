package edu.flash3388.flashlib.robot.actions;

import edu.flash3388.flashlib.robot.SourceAction;
import edu.flash3388.flashlib.robot.devices.DoubleDataSource;

public class RotateActionPart extends SourceAction{

	private DoubleDataSource speedSource;
	
	public RotateActionPart(DoubleDataSource speed){
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
