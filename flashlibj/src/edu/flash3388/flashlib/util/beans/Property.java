package edu.flash3388.flashlib.util.beans;

/**
 * An interface pointing to a value. Such objects are used to point to changeable values and allow 
 * for real time updating of the value instead of a need for set methods. Unlike a source, a property
 * allows setting the value as well as getting.
 * 
 * @author tom Tzook
 * @param <T> type of value pointed to by this property
 * @since FlashLib 1.0.1
 */
public interface Property<T> extends ValueSource<T> {

	/**
	 * Sets the value of the property. Implementation is user dependent.
	 * @param o the new value
	 */
	void setValue(T o);
}
