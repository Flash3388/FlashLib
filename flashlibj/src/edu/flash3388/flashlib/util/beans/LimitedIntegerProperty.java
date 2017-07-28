package edu.flash3388.flashlib.util.beans;

import edu.flash3388.flashlib.math.Mathf;

/**
 * A setter and getter bean for int value which limits data available.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class LimitedIntegerProperty extends SimpleIntegerProperty{
	
	private int upperLimit, lowerLimit;
	
	public LimitedIntegerProperty(int initialVal, int lowerLimit, int upperLimit){
		super(0);
		set(initialVal);
	}
	public LimitedIntegerProperty(int lowerLimit, int upperLimit){
		this(0, lowerLimit, upperLimit);
	}
	
	public void setLowerLimit(int lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public void setUpperLimit(int upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	public int getLowerLimit() {
		return lowerLimit;
	}
	public int getUpperLimit() {
		return upperLimit;
	}
	
	@Override
	public void set(int var) {
		super.set((int)Mathf.constrain(var, lowerLimit, upperLimit));
	}
}
