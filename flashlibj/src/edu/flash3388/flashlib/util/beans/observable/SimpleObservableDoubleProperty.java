package edu.flash3388.flashlib.util.beans.observable;

public class SimpleObservableDoubleProperty extends ObservableDoubleProperty{

	private double value;
	
	public SimpleObservableDoubleProperty(double initialValue){
		this.value = initialValue;
	}
	public SimpleObservableDoubleProperty() {
		this(0.0);
	}
	
	@Override
	protected double getInternal() {
		return value;
	}
	@Override
	protected void setInternal(double d) {
		this.value = d;
	}
}
