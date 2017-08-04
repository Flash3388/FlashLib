package edu.flash3388.flashlib.util.beans.observables;

public class SimpleObservableDoubleProperty extends ObservableDoubleProperty{

	private double value;
	
	public SimpleObservableDoubleProperty(double initialValue){
		this.value = initialValue;
	}
	public SimpleObservableDoubleProperty() {
		this(0.0);
	}
	
	@Override
	public double get() {
		return value;
	}
	@Override
	public void set(double d) {
		this.value = d;
	}
}
