package edu.flash3388.flashlib.util.beans;

/**
 * A double getter and setter bean which contains a variable. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SimpleDoubleProperty implements DoubleProperty{

	private double var;
	
	public SimpleDoubleProperty(double initialVal){
		var = initialVal;
	}
	public SimpleDoubleProperty(){
		this(0);
	}
	
	@Override
	public void set(double var){
		this.var = var;
	}
	@Override
	public double get(){
		return var;
	}
	@Override
	public void setValue(Double o) {
		set(o == null? 0.0 : o.doubleValue());
	}
	@Override
	public Double getValue() {
		return var;
	}
}
