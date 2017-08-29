package edu.flash3388.flashlib.util.beans.observable;

/**
 * 
 * ChangeListener is used to listen to changes of observable values, i.e. observing them.
 * A listener can be attached to an observable value by calling {@link ObservableValue#addListener(ChangeListener)} and
 * removed using {@link ObservableValue#removeListener(ChangeListener)}.
 * 
 * @author Tom Tzook
 * @param <T> type of the observable value 
 * @since FlashLib 1.0.1
 */
@FunctionalInterface
public interface ChangeListener<T> {

	/**
	 * Called when the value of the observable changes. 
	 * 
	 * @param observable the observable value this listener was observing.
	 * @param oldValue the old value of the observable
	 * @param newValue the new value of the observable
	 */
	void changed(ObservableValue<? extends T> observable, T oldValue, T newValue);
}
