package edu.flash3388.flashlib.util.beans;

/**
 * An interface pointing to a String value. Such objects are used to point to changeable values and allow 
 * for real time updating of the value instead of a need for set methods. Unlike a source, a property
 * allows setting the value as well as getting.
 * 
 * @author tom Tzook
 * @since FlashLib 1.0.1
 */
public interface StringProperty extends Property<String>, StringSource{

	/**
	 * Sets the value of the property. Implementation is user dependent.
	 * @param s new value
	 */
	void set(String s);
}
