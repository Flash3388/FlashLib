package edu.flash3388.flashlib.util.beans.observable;

import edu.flash3388.flashlib.util.beans.DoubleSource;

/**
 * ObservableDoubleValue is an combination of an {@link ObservableValue} with a double type and a {@link DoubleSource}.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface ObservableDoubleValue extends ObservableValue<Double>, DoubleSource{
}
