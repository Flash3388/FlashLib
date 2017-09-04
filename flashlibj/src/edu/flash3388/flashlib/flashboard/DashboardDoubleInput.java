package edu.flash3388.flashlib.flashboard;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.StringProperty;

/**
 * Represents input for double values on the flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class DashboardDoubleInput extends DashboardStringInput implements StringProperty{

	private DoubleProperty property;
	
	public DashboardDoubleInput(String name, DoubleProperty property) {
		super(name, null);
		this.property = property;
		setProperty(this);
		setType(InputType.Double);
	}

	public DoubleProperty numberValueProperty(){
		return property;
	}
	
	@Override
	public void set(String s) {
		property.set(FlashUtil.toDouble(s, property.get()));
	}
	@Override
	public String get() {
		return String.valueOf(property.get());
	}
	@Override
	public String getValue() {
		return get();
	}
	@Override
	public void setValue(String s) {
		set(s);
	}
}
