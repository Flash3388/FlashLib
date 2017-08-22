package edu.flash3388.flashlib.util.beans.observable;

import edu.flash3388.flashlib.util.beans.StringProperty;

/**
 * 
 * An abstract implementation of an {@link ObservableProperty} for String values. Implements all requirements for 
 * being observable but leaves the implementation of the getter and setter of the property's value user dependent.
 * When extending this, implement {@link #setInternal(String)} for setting the value and {@link #getInternal()} for
 * getting the value. The rest must remain untouched for the current implementation of the observable to function.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public abstract class ObservableStringProperty extends ObservablePropertyBase<String> implements ObservableStringValue, StringProperty{
	@Override
	public String get() {
		return getValue();
	}
	@Override
	public void set(String s) {
		setValue(s);
	}
}
