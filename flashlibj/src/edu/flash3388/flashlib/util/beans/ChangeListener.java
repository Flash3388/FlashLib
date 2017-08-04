package edu.flash3388.flashlib.util.beans;

import edu.flash3388.flashlib.util.beans.observables.ObservableValue;

@FunctionalInterface
public interface ChangeListener<T> {

	void changed(ObservableValue<? extends T> observable, T oldValue, T newValue);
}
