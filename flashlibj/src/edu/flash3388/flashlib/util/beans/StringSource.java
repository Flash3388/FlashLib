package edu.flash3388.flashlib.util.beans;

/**
 * A getter bean for string data.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface StringSource {
	
	/**
	 * Gets the value. Implementation is user dependent.
	 * @return value
	 */
	String get();
}
