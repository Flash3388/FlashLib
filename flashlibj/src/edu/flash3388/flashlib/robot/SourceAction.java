package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.robot.devices.DoubleDataSource;

/**
 * Source action is mainly used for combined actions. It contains a double data source
 * whose value can be changed by extending classes. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class SourceAction extends Action{
	
	protected final DoubleDataSource.VarDataSource dataSource;
	
	public SourceAction(){
		dataSource = new DoubleDataSource.VarDataSource(0);
	}
	
	public DoubleDataSource getSource(){
		return dataSource;
	}
}
