package edu.flash3388.flashlib.util.beans.observables;

import edu.flash3388.flashlib.util.beans.ChangeListener;
import edu.flash3388.flashlib.util.beans.DoubleProperty;
import edu.flash3388.flashlib.util.beans.PropertyHelper;

public abstract class ObservableDoubleProperty implements ObservableProperty<Double>, ObservableDoubleValue, DoubleProperty{

	private ObservableDoubleValue observable = null;
	private PropertyHelper<Double> helper = null;
	private ChangeListener<Double> listener = null;
	
	@Override
	public void fireValueChangedEvent() {
        PropertyHelper.fireValueChangedEvent(helper);
    }
	
	@Override
	public void setValue(Double o) {
		if(isBound())
			throw new RuntimeException("This property is bound and cannot be set");
		if(o == null)
			o = 0.0;
		if(get() != o){
			set(o);
			fireValueChangedEvent();
		}
	}
	@Override
	public Double getValue() {
		return observable == null? get() : observable.get();
	}

	@Override
	public void addListener(ChangeListener<? super Double> listener) {
		helper = PropertyHelper.addListener(helper, this, listener);
		
	}
	@Override
	public void removeListener(ChangeListener<? super Double> listener) {
		helper = PropertyHelper.removeListener(helper, listener);
	}

	@Override
	public void bind(ObservableValue<? extends Double> observable) {
		if(observable == null)
			throw new NullPointerException("cannot bind to null");
		
		if(!observable.equals(this.observable)){
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
