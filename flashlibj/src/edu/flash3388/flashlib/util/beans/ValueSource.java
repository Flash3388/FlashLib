package edu.flash3388.flashlib.util.beans;

import java.io.Serializable;

/**
 * An interface pointing to a value. Such objects are used to point to changeable values and allow 
 * for real time updating of the value instead of a need for set methods.
 * 
 * @author tom Tzook
 * @param <T> type of value pointed to by this interface
 * @since FlashLib 1.0.1
 */
@FunctionalInterface
public interface ValueSource<T> extends Serializable {
	
	/**
	 * Gets the value pointed to by this object. Implementation is user dependent.
	 * 
	 * @return value stored in the source.
	 */
	T getValue();
}
