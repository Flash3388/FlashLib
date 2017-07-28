package edu.flash3388.flashlib.util.beans;

/**
 * Represents a getter and setter bean for primitive double values. Extends DoubleSource.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public interface DoubleProperty extends DoubleSource{

	/**
	 * Sets the value of the bean. Implementation is user dependent.
	 * @param d new value
	 */
	void set(double d);
}
