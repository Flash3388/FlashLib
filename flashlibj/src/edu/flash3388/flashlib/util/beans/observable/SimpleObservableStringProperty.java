package edu.flash3388.flashlib.util.beans.observable;

/**
 * A simple implementation of {@link ObservableStringProperty} using a String variable which
 * stores the property's value.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
@SuppressWarnings("serial")
public class SimpleObservableStringProperty extends ObservableStringProperty{

	private String value;
	
	public SimpleObservableStringProperty(String initialValue){
		setInternal(initialValue);
	}
	public SimpleObservableStringProperty() {
		this("");
	}
	
	@Override
	protected void setInternal(String val) {
		value = val == null? "" : val;
	}
	@Override
	protected String getInternal() {
		return value;
	}
}
