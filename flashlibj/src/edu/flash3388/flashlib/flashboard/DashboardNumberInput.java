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
public class DashboardNumberInput extends DashboardStringInput implements StringProperty{

	private DoubleProperty property;
	
	public DashboardNumberInput(String name, DoubleProperty property) {
		super(name, null, InputType.Double);
		this.property = property;
		setProperty(this);
	}

	public DoubleProperty numberValueProperty(){
		return property;
	}
	
	@Override
	public String get() {
		return String.valueOf(property.get());
	}

	@Override
	public void set(String s) {
		property.set(FlashUtil.toDouble(s, property.get()));
	}
}