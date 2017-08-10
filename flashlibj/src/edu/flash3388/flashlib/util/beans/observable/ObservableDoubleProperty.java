package edu.flash3388.flashlib.util.beans.observable;

import edu.flash3388.flashlib.util.beans.DoubleProperty;

/**
 * 
 * An abstract implementation of an {@link ObservableProperty} for double values. Implements all requirements for 
 * being observable but leaves the implementation of the getter and setter of the property's value user dependent.
 * When extending this, implement {@link #setInternal(double)} for setting the value and {@link #getInternal()} for
 * getting the value. The rest must remain untouched for the current implementation of the observable to function.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public abstract class ObservableDoubleProperty implements ObservableProperty<Double>, ObservableDoubleValue, DoubleProperty{

	private ObservableDoubleValue observable = null;
	private ObservablePropertyHelper<Double> helper = null;
	private ChangeListener<Double> listener = null;
	
	
	protected abstract void setInternal(double d);
	protected abstract double getInternal();
	
	@Override
	public void set(double d) {
		if(isBound())
			throw new RuntimeException("This property is bound and cannot be set");
		if(get() != d){
			setInternal(d);
			fireValueChangedEvent();
		}
	}
	@Override
	public double get() {
		return observable == null? getInternal() : observable.get();
	}
	@Override
	public void setValue(Double o) {
		set(o == null? 0.0 : o);
	}
	@Override
	public Double getValue() {
		return get();
	}

	@Override
	public void fireValueChangedEvent() {
        ObservablePropertyHelper.fireValueChangedEvent(helper);
    }

	@Override
	public void addListener(ChangeListener<? super Double> listener) {
		helper = ObservablePropertyHelper.addListener(helper, this, listener);
		
	}
	@Override
	public void removeListener(ChangeListener<? super Double> listener) {
		helper = ObservablePropertyHelper.removeListener(helper, listener);
	}

	@Override
	public void bind(ObservableValue<? extends Double> observable) {
		if(observable == null)
			throw new NullPointerException("cannot bind to null");
		
		if(this.observable == null || !observable.equals(this.observable)){
			if (isBound())
				unbind();
			
			this.observable = (ObservableDoubleValue) observable;
			if(listener == null)
				listener = new ObservableChangeListener<Double>(this);
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
