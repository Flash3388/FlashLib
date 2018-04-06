package edu.flash3388.flashlib.util.beans;

/**
 * An interface pointing to a primitive boolean value. Such objects are used to point to changeable values and allow 
 * for real time updating of the value instead of a need for set methods. Unlike a source, a property
 * allows setting the value as well as getting.
 * 
 * @author tom Tzook
 * @since FlashLib 1.0.1
 */
public interface BooleanProperty extends Property<Boolean>, BooleanSource {
	
	/**
	 * Sets the value of the property. Implementation is user dependent.
	 * @param b new value
	 */
	void set(boolean b);
	
	
	default public void setValue(Boolean o) {
		set(o == null? false : o.booleanValue());
	}
	default public Boolean getValue() {
		return get();
	}
}
