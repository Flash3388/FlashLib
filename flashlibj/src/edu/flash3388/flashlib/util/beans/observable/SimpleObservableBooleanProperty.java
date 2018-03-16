package edu.flash3388.flashlib.util.beans.observable;

/**
 * A simple implementation of {@link ObservableBooleanProperty} using a primitive boolean variable which
 * stores the property's value.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
@SuppressWarnings("serial")
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
