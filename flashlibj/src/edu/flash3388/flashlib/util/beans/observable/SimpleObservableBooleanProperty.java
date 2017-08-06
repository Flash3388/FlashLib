package edu.flash3388.flashlib.util.beans.observable;

public class SimpleObservableBooleanProperty extends ObservableBooleanProperty{

	private boolean value;
	
	public SimpleObservableBooleanProperty(boolean initialValue){
		this.value = initialValue;
	}
	public SimpleObservableBooleanProperty() {
		this(false);
	}
	
	@Override
	protected boolean getInternal() {
		return value;
	}
	@Override
	protected void setInternal(boolean b) {
		this.value = b;
	}
}
