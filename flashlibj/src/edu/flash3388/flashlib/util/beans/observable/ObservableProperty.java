package edu.flash3388.flashlib.util.beans.observable;

import edu.flash3388.flashlib.util.beans.Property;

/**
 * ObserableProperty is a {@link Property} which can be listened to. It extends {@link ObservableValue} as
 * well as {@link Property}. Unlike a simple {@link ObservableValue}, an ObservableProperty can be bound to 
 * an ObservableValue meaning that the value of the bound source is now this property's value as well. When
 * bound, it is not possible to change the property's values. Unbinding the property restores it's old value before
 * the binding.
 * 
 * @author Tom Tzook
 * @param <T> type of value of this property
 * @since FlashLib 1.0.1
 */
public interface ObservableProperty<T> extends Property<T>, ObservableValue<T>{

	/**
	 * Binds this property to an {@link ObservableValue}. Doing so causes this property to point to
	 * the bound observable's value. This also means it is not possible to set this property's value.
	 * If this property is already bound, it will be unbound first.
	 * 
	 * @param observable Observable to bind to
	 */
	void bind(ObservableValue<? extends T> observable);
	/**
	 * Unbinds this property from the bound observable, if such exists. This effectively reverts the property to
	 * its pre-bind value and re-allows set of that value.
	 */
	void unbind();
	/**
	 * Gets whether or not this property is bound to an observable.
	 * @return true if the property is bound, false otherwise.
	 */
	boolean isBound();
	
	/**
	 * Do not call this manually. EVER!!
	 */
	void fireValueChangedEvent();
}
