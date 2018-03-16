package edu.flash3388.flashlib.util.beans;

/**
 * An interface pointing to a primitive double value. Such objects are used to point to changeable values and allow 
 * for real time updating of the value instead of a need for set methods. Unlike a source, a property
 * allows setting the value as well as getting.
 * 
 * @author tom Tzook
 * @since FlashLib 1.0.1
 */
public interface DoubleProperty extends Property<Double>, DoubleSource {

	/**
	 * Sets the value of the property. Implementation is user dependent.
	 * @param d new value
	 */
	void set(double d);
}
