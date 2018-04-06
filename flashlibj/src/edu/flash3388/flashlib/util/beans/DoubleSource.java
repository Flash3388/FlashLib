package edu.flash3388.flashlib.util.beans;

import java.io.Serializable;

/**
 * An interface pointing to a primitive double value. Such objects are used to point to changeable values and allow 
 * for real time updating of the value instead of a need for set methods.
 * 
 * @author tom Tzook
 * @since FlashLib 1.0.1
 */
@FunctionalInterface
public interface DoubleSource extends Serializable {
	
	/**
	 * Gets the value of this source. Implementation is user dependent.
	 * @return value
	 */
	double get();
}
