package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.BooleanProperty;
import edu.flash3388.flashlib.util.beans.StringProperty;

/**
 * Represents input for boolean values on the flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class DashboardBooleanInput extends DashboardStringInput implements StringProperty{

	private BooleanProperty property;
	
	public DashboardBooleanInput(String name, BooleanProperty property) {
		super(name, null, InputType.Boolean);
		this.property = property;
		setProperty(this);
	}
	
	public BooleanProperty booleanValueProperty(){
		return property;
	}
	
	@Override
	public String get() {
		return String.valueOf(property.get());
	}
	@Override
	public void set(String s) {
		property.set(FlashUtil.toBoolean(s, property.get()));
	}
}
