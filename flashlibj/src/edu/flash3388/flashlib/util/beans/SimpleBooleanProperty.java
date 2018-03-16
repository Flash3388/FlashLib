package edu.flash3388.flashlib.util.beans;

/**
 * A simple implementation of {@link BooleanProperty}. Holds a primitive boolean variable which can be
 * set or get.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class SimpleBooleanProperty implements BooleanProperty {
	
	private boolean var;
	
	public SimpleBooleanProperty(boolean initialVal){
		var = initialVal;
	}
	public SimpleBooleanProperty(){
		this(false);
	}
	
	public boolean switchValue(){
		var = !var;
		return var;
	}
	
	@Override
	public void set(boolean b){
		var = b;
	}
	@Override
	public boolean get() {
		return var;
	}
	@Override
	public void setValue(Boolean o) {
		set(o == null? false : o.booleanValue());
	}
	@Override
	public Boolean getValue() {
		return var;
	}
	
	@Override
	public String toString() {
		return String.valueOf(var);
	}
}
