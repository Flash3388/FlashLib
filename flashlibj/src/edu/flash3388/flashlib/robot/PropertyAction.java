package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.SimpleDoubleProperty;

/**
 * Property action is mainly used for combined actions. It extends {@link SourceAction} and holds a value 
 * property whose value is returned in {@link SourceAction#get()}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.2.0
 */
public abstract class PropertyAction extends SourceAction {

	private DoubleProperty property;
	
	public PropertyAction() {
		this(new SimpleDoubleProperty());
	}
	public PropertyAction(DoubleProperty property) {
		this.property = property;
	}
	
	public DoubleProperty valueProperty(){
		return property;
	}
	
	@Override
	public double get() {
		return property.get();
	}
}
