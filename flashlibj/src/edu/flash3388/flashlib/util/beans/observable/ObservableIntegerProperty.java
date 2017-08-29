package edu.flash3388.flashlib.util.beans.observable;

import edu.flash3388.flashlib.util.beans.IntegerProperty;

/**
 * 
 * An abstract implementation of an {@link ObservableProperty} for integer values. Implements all requirements for 
 * being observable but leaves the implementation of the getter and setter of the property's value user dependent.
 * When extending this, implement {@link #setInternal(int)} for setting the value and {@link #getInternal()} for
 * getting the value. The rest must remain untouched for the current implementation of the observable to function.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public abstract class ObservableIntegerProperty implements ObservableProperty<Integer>, ObservableIntegerValue, IntegerProperty{

	private ObservableIntegerValue observable = null;
	private ObservablePropertyHelper<Integer> helper = null;
	private ChangeListener<Integer> listener = null;
	
	
	protected abstract void setInternal(int i);
	protected abstract int getInternal();
	
	@Override
	public void set(int i) {
		if(isBound())
			throw new RuntimeException("This property is bound and cannot be set");
		if(get() != i){
			setInternal(i);
			fireValueChangedEvent();
		}
	}
	@Override
	public int get() {
		return observable == null? getInternal() : observable.get();
	}
	@Override
	public void setValue(Integer o) {
		set(o == null? 0 : o);
	}
	@Override
	public Integer getValue() {
		return get();
	}

	@Override
    public void fireValueChangedEvent() {
        ObservablePropertyHelper.fireValueChangedEvent(helper);
    }
	@Override
	public void addListener(ChangeListener<? super Integer> listener) {
		helper = ObservablePropertyHelper.addListener(helper, this, listener);
		
	}
	@Override
	public void removeListener(ChangeListener<? super Integer> listener) {
		helper = ObservablePropertyHelper.removeListener(helper, listener);
	}

	@Override
	public void bind(ObservableValue<? extends Integer> observable) {
		if(observable == null)
			throw new NullPointerException("cannot bind to null");
		
		if(this.observable == null || !observable.equals(this.observable)){
			if (isBound())
				unbind();
			
			this.observable = (ObservableIntegerValue) observable;
			if(listener == null)
				listener = new ObservableChangeListener<Integer>(this);
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
