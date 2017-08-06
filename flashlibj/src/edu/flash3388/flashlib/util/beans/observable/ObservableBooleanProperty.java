package edu.flash3388.flashlib.util.beans.observable;

import edu.flash3388.flashlib.util.beans.BooleanProperty;

public abstract class ObservableBooleanProperty implements ObservableProperty<Boolean>, ObservableBooleanValue, BooleanProperty{

	private ObservableBooleanValue observable = null;
	private ObservablePropertyHelper<Boolean> helper = null;
	private ChangeListener<Boolean> listener = null;
	
	
	protected abstract void setInternal(boolean b);
	protected abstract boolean getInternal();
	
	@Override
	public void set(boolean b) {
		if(isBound())
			throw new RuntimeException("This property is bound and cannot be set");
		if(get() != b){
			setInternal(b);
			fireValueChangedEvent();
		}
	}
	@Override
	public boolean get() {
		return observable == null? getInternal() : observable.get();
	}
	@Override
	public void setValue(Boolean o) {
		set(o == null? false : o);
	}
	@Override
	public Boolean getValue() {
		return get();
	}

	@Override
	public void fireValueChangedEvent() {
        ObservablePropertyHelper.fireValueChangedEvent(helper);
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
		
		if(this.observable == null || !observable.equals(this.observable)){
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
