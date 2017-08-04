package edu.flash3388.flashlib.util.beans.observables;

import edu.flash3388.flashlib.util.beans.ChangeListener;
import edu.flash3388.flashlib.util.beans.ValueSource;

public interface ObservableValue<T> extends ValueSource<T> {

	void addListener(ChangeListener<? super T> listener);
	void removeListener(ChangeListener<? super T> listener);
}
