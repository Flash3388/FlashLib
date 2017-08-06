package edu.flash3388.flashlib.util.beans.observable;

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
