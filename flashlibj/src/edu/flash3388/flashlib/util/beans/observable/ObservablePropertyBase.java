package edu.flash3388.flashlib.util.beans.observable;

/**
 * 
 * An abstract implementation of an {@link ObservableProperty} for generic values. Implements all requirements for 
 * being observable but leaves the implementation of the getter and setter of the property's value user dependent.
 * When extending this, implement {@link #setInternal(Object)} for setting the value and {@link #getInternal()} for
 * getting the value. The rest must remain untouched for the current implementation of the observable to function.
 * 
 * @param <T> the property's value type
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public abstract class ObservablePropertyBase<T> implements ObservableProperty<T>{

	private ObservableValue<T> observable = null;
	private ObservablePropertyHelper<T> helper = null;
	private ChangeListener<T> listener = null;
	
	protected abstract void setInternal(T val);
	protected abstract T getInternal();
	
	@Override
    public void fireValueChangedEvent() {
        ObservablePropertyHelper.fireValueChangedEvent(helper);
    }
	
	@Override
	public void setValue(T o) {
		if(isBound())
			throw new RuntimeException("This property is bound and cannot be set");
		T value = getValue();
		if((value == null && o != null) || (o != null? !value.equals(o) : true)){
			setInternal(o);
			fireValueChangedEvent();
		}
	}
	@Override
	public T getValue() {
		return observable == null? getInternal() : observable.getValue();
	}

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		helper = ObservablePropertyHelper.addListener(helper, this, listener);
	}
	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		helper = ObservablePropertyHelper.removeListener(helper, listener);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void bind(ObservableValue<? extends T> observable) {
		if(observable == null)
			throw new NullPointerException("cannot bind to null");
		
		if(this.observable == null || !observable.equals(this.observable)){
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
			observable.removeListener(listener);
			observable = null;
			setValue(observable.getValue());
		}
	}
	@Override
	public boolean isBound() {
		return observable != null;
	}
}
