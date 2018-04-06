package edu.flash3388.flashlib.util.beans;

/**
 * An interface pointing to a String value. Such objects are used to point to changeable values and allow 
 * for real time updating of the value instead of a need for set methods.
 * 
 * @author tom Tzook
 * @since FlashLib 1.0.1
 */
public interface StringSource extends ValueSource<String> {

	/**
	 * Gets the value of this source. Implementation is user dependent.
	 * @return value
	 */
	String get();
}
