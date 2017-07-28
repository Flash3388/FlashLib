package edu.flash3388.flashlib.util.beans;

/**
 * A getter bean for double data.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface DoubleSource {
	
	/**
	 * Gets the value. Implementation is user dependent.
	 * @return value
	 */
	double get();
}
