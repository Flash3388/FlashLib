package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.devices.DoubleDataSource;

public abstract class SourceAction extends Action{
	protected DoubleDataSource.VarDataSource dataSource = 
			new DoubleDataSource.VarDataSource(0);
	
	public DoubleDataSource getSource(){
		return dataSource;
	}
}
