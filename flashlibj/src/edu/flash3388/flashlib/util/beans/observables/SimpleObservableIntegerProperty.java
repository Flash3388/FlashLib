package edu.flash3388.flashlib.util.beans.observables;

public class SimpleObservableIntegerProperty extends ObservableIntegerProperty{

	private int value;
	
	public SimpleObservableIntegerProperty(int initialValue){
		this.value = initialValue;
	}
	public SimpleObservableIntegerProperty() {
		this(0);
	}
	
	@Override
	public int get() {
		return value;
	}
	@Override
	public void set(int i) {
		this.value = i;
	}
}
