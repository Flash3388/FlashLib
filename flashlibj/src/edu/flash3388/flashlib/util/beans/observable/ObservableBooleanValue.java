package edu.flash3388.flashlib.util.beans.observable;

import edu.flash3388.flashlib.util.beans.BooleanSource;

/**
 * ObservableBooleanValue is an combination of an {@link ObservableValue} with a boolean type and a {@link BooleanSource}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface ObservableBooleanValue extends ObservableValue<Boolean>, BooleanSource{
}
