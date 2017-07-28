package edu.flash3388.flashlib.util.beans;

/**
 * A getter bean for boolean data.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
@FunctionalInterface
public interface BooleanSource{
	
	/**
	 * Gets the value. Implementation is user dependent.
	 * @return value
	 */
	boolean get();
}
