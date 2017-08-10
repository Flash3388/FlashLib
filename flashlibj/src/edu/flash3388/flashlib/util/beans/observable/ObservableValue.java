package edu.flash3388.flashlib.util.beans.observable;

import edu.flash3388.flashlib.util.beans.ValueSource;

/**
 * ObservableValue is a {@link ValueSource} which can be listened to using {@link ChangeListener}s, allowing to
 * track changes of values.
 * 
 * @author Tom Tzook
 * @param <T> type of value of this source
 * @since FlashLib 1.0.1
 */
public interface ObservableValue<T> extends ValueSource<T> {

	/**
	 * Adds a new listener to track value changes of this value source.
	 * 
	 * @param listener a change listener to add
	 */
	void addListener(ChangeListener<? super T> listener);
	/**
	 * Removes a listener from this observable. If the given listener was never attached, nothing will occur.
	 * 
	 * @param listener a listener to remove if was add
	 */
	void removeListener(ChangeListener<? super T> listener);
}
