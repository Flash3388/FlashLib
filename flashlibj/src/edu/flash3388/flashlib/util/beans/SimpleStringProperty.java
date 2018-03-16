package edu.flash3388.flashlib.util.beans;

/**
 * A simple implementation of {@link StringProperty}. Holds a String variable which can be
 * set or get.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
public class SimpleStringProperty implements StringProperty{
	
	private String var;
	
	public SimpleStringProperty(String initialVal){
		set(initialVal);
	}
	public SimpleStringProperty(){
		this("");
	}
	
	@Override
	public String get() {
		return var;
	}
	@Override
	public void set(String s) {
		this.var = s == null? "" : s;
	}
	@Override
	public void setValue(String o) {
		set(o);
	}
	@Override
	public String getValue() {
		return var;
	}
	
	@Override
	public String toString() {
		return var;
	}
}
