package edu.flash3388.flashlib.util.beans.observable;

/**
 * A simple implementation of {@link ObservableIntegerProperty} using a primitive integer variable which
 * stores the property's value.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class SimpleObservableIntegerProperty extends ObservableIntegerProperty{

	private int value;
	
	public SimpleObservableIntegerProperty(int initialValue){
		this.value = initialValue;
	}
	public SimpleObservableIntegerProperty() {
		this(0);
	}
	
	@Override
	protected int getInternal() {
		return value;
	}
	@Override
	protected void setInternal(int i) {
		this.value = i;
	}
}
