package edu.flash3388.flashlib.util.beans.observables;

import edu.flash3388.flashlib.util.beans.Property;

public interface ObservableProperty<T> extends Property<T>, ObservableValue<T>{

	void bind(ObservableValue<? extends T> observable);
	void unbind();
	boolean isBound();
	
	void fireValueChangedEvent();
}
