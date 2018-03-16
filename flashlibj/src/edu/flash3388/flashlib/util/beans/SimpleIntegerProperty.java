package edu.flash3388.flashlib.util.beans;

/**
 * A simple implementation of {@link IntegerProperty}. Holds a primitive integer variable which can be
 * set or get.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
@SuppressWarnings("serial")
public class SimpleIntegerProperty implements IntegerProperty{
	
	private int var;
	
	public SimpleIntegerProperty(int initialVal){
		var = initialVal;
	}
	public SimpleIntegerProperty(){
		this(0);
	}
	
	@Override
	public int get() {
		return var;
	}
	@Override
	public void set(int i) {
		this.var = i;
	}
	@Override
	public void setValue(Integer o) {
		set(o == null? 0 : o.intValue());
	}
	@Override
	public Integer getValue() {
		return var;
	}
	
	@Override
	public String toString() {
		return String.valueOf(var);
	}
}
