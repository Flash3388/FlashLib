package edu.flash3388.flashlib.util.beans.observable;

import java.lang.ref.WeakReference;

public class ObservableChangeListener<T> implements ChangeListener<T>{
	
	private final WeakReference<ObservableProperty<T>> wref;

	public ObservableChangeListener(ObservableProperty<T> prop) {
		wref = new WeakReference<ObservableProperty<T>>(prop);
	}
	
	@Override
	public void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		ObservableProperty<T> prop = wref.get();
		if(prop == null)
			observable.removeListener(this);
		else
			prop.fireValueChangedEvent();
	}
}
