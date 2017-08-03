package edu.flash3388.flashlib.util.beans;

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
