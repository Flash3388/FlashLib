package edu.flash3388.flashlib.util.beans.observables;

import edu.flash3388.flashlib.util.beans.ChangeListener;
import edu.flash3388.flashlib.util.beans.PropertyHelper;

public class SimpleObservableProperty<T> implements ObservableProperty<T>{

	private T value;
	private ObservableValue<T> observable = null;
	private PropertyHelper<T> helper = null;
	private ChangeListener<T> listener = null;
	
	
	public SimpleObservableProperty(T initialValue){
		this.value = initialValue;
	}
	
	@Override
    public void fireValueChangedEvent() {
        PropertyHelper.fireValueChangedEvent(helper);
    }
	
	@Override
	public void setValue(T o) {
		if(isBound())
			throw new RuntimeException("This property is bound and cannot be set");
		if(!value.equals(o)){
			value = o;
			fireValueChangedEvent();
		}
	}
	@Override
	public T getValue() {
		return observable == null? value : observable.getValue();
	}

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		helper = PropertyHelper.addListener(helper, this, listener);
	}
	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		helper = PropertyHelper.removeListener(helper, listener);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void bind(ObservableValue<? extends T> observable) {
		if(observable == null)
			throw new NullPointerException("cannot bind to null");
		
		if(!observable.equals(this.observable)){
			if (isBound())
				unbind();
			
			this.observable = (ObservableValue<T>) observable;
			if(listener == null)
				listener = new ObservableChangeListener<T>(this);
			observable.addListener(listener);
		}
	}
	@Override
	public void unbind() {
		if(isBound()){
			value = observable.getValue();
			observable.removeListener(listener);
			observable = null;
		}
	}
	@Override
	public boolean isBound() {
		return observable != null;
	}
}
