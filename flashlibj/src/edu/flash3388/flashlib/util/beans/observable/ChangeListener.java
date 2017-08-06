package edu.flash3388.flashlib.util.beans.observable;

@FunctionalInterface
public interface ChangeListener<T> {

	void changed(ObservableValue<? extends T> observable, T oldValue, T newValue);
}
