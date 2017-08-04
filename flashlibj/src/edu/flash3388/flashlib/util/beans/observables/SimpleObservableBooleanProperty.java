package edu.flash3388.flashlib.util.beans.observables;

public class SimpleObservableBooleanProperty extends ObservableBooleanProperty{

	private boolean value;
	
	public SimpleObservableBooleanProperty(boolean initialValue){
		this.value = initialValue;
	}
	public SimpleObservableBooleanProperty() {
		this(false);
	}
	
	@Override
	public boolean get() {
		return value;
	}
	@Override
	public void set(boolean b) {
		this.value = b;
	}
}
