package edu.flash3388.flashlib.util.beans;

/**
 * Represents a getter and setter bean for string values. Extends StringSource.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface StringProperty extends StringSource{

	/**
	 * Sets the value of the bean. Implementation is user dependent.
	 * @param s new value
	 */
	void set(String s);
}
