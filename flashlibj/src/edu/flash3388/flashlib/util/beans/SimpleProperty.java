package edu.flash3388.flashlib.util.beans;

/**
 * A simple implementation of {@link Property}. Holds a variable of the generic type which can be
 * set or get.
 * 
 * @author Tom Tzook
 * @param <T> type of value of this property
 * @since FlashLib 1.0.1
 */
@SuppressWarnings("serial")
public class SimpleProperty<T> implements Property<T> {

	private T value;
	
	public SimpleProperty(T value){
		this.value = value;
	}
	public SimpleProperty() {}
	
	@Override
	public T getValue() {
		return value;
	}
	@Override
	public void setValue(T o) {
		this.value = o;
	}
	
	@Override
	public String toString() {
		return value.toString();
	}
}
