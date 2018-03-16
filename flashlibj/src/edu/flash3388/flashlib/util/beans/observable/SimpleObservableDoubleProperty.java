package edu.flash3388.flashlib.util.beans.observable;

/**
 * A simple implementation of {@link ObservableDoubleProperty} using a primitive double variable which
 * stores the property's value.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
@SuppressWarnings("serial")
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
