package edu.flash3388.flashlib.util.beans;

/**
 * Represents a getter and setter bean for primitive boolean values. Extends BooleanSource.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface BooleanProperty extends BooleanSource{
	
	/**
	 * Sets the value of the bean. Implementation is user dependent.
	 * @param b new value
	 */
	void set(boolean b);
}
