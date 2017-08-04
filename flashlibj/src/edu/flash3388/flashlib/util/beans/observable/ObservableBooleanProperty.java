package edu.flash3388.flashlib.util.beans.observable;

import edu.flash3388.flashlib.util.beans.ChangeListener;
import edu.flash3388.flashlib.util.beans.BooleanProperty;

public abstract class ObservableBooleanProperty implements ObservableProperty<Boolean>, ObservableBooleanValue, BooleanProperty{

	private ObservableBooleanValue observable = null;
	private ObservablePropertyHelper<Boolean> helper = null;
	private ChangeListener<Boolean> listener = null;
	
	@Override
	public void fireValueChangedEvent() {
        ObservablePropertyHelper.fireValueChangedEvent(helper);
    }
	
	@Override
	public void setValue(Boolean o) {
		if(isBound())
			throw new RuntimeException("This property is bound and cannot be set");
		if(o == null)
			o = false;
		if(get() != o){
			set(o);
			fireValueChangedEvent();
		}
	}
	@Override
	public Boolean getValue() {
		return observable == null? get() : observable.get();
	}

	@Override
	public void addListener(ChangeListener<? super Boolean> listener) {
		helper = ObservablePropertyHelper.addListener(helper, this, listener);
		
	}
	@Override
	public void removeListener(ChangeListener<? super Boolean> listener) {
		helper = ObservablePropertyHelper.removeListener(helper, listener);
	}

	@Override
	public void bind(ObservableValue<? extends Boolean> observable) {
		if(observable == null)
			throw new NullPointerException("cannot bind to null");
		
		if(!observable.equals(this.observable)){
			if (isBound())
				unbind();
			
			this.observable = (ObservableBooleanValue) observable;
			if(listener == null)
				listener = new ObservableChangeListener<Boolean>(this);
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
