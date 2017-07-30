package edu.flash3388.flashlib.util.beans;

/**
 * A getter bean for int data.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
@FunctionalInterface
public interface IntegerSource {

	/**
	 * Gets the value. Implementation is user dependent.
	 * @return value
	 */
	int get();
}
