package edu.flash3388.flashlib.util.beans;

import edu.flash3388.flashlib.math.Mathf;

/**
 * A setter and getter bean for double value which limits data available.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class LimitedDoubleProperty extends SimpleDoubleProperty{

	private double upperLimit, lowerLimit;
	
	public LimitedDoubleProperty(double initialVal, double lowerLimit, double upperLimit){
		super(0);
		set(initialVal);
	}
	public LimitedDoubleProperty(double lowerLimit, double upperLimit){
		this(0, lowerLimit, upperLimit);
	}
	
	public void setLowerLimit(double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}
	public void setUpperLimit(double upperLimit) {
		this.upperLimit = upperLimit;
	}
	
	public double getLowerLimit() {
		return lowerLimit;
	}
	public double getUpperLimit() {
		return upperLimit;
	}
	
	@Override
	public void set(double var) {
		super.set(Mathf.constrain(var, lowerLimit, upperLimit));
	}
}
