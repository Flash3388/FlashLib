package edu.flash3388.flashlib.util.beans;

/**
 * Represents a getter and setter bean for primitive int values. Extends IntegerSource.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface IntegerProperty extends IntegerSource{

	/**
	 * Sets the value of the bean. Implementation is user dependent.
	 * @param i new value
	 */
	void set(int i);
}
