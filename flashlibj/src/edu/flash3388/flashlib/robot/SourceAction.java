package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.DoubleSource;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;

/**
 * Source action is mainly used for combined actions. It contains a double data source
 * whose value can be changed by extending classes. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public abstract class SourceAction extends Action{
	
	protected final DoubleProperty dataSource;
	
	public SourceAction(){
		dataSource = new SimpleDoubleProperty(0);
	}
	
	public DoubleSource getSource(){
		return dataSource;
	}
}
