package edu.flash3388.flashlib.util.beans.observable;

/**
 * A simple implementation of {@link ObservablePropertyBase} using a generic variable which
 * stores the property's value.
 * 
 * @param <T> the property's value type
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class SimpleObservableProperty<T> extends ObservablePropertyBase<T>{

	private T value;
	
	public SimpleObservableProperty(){
		this(null);
	}
	public SimpleObservableProperty(T initialValue){
		this.value = initialValue;
	}

	@Override
	protected void setInternal(T val) {
		value = val;
	}
	@Override
	protected T getInternal() {
		return value;
	}
}
