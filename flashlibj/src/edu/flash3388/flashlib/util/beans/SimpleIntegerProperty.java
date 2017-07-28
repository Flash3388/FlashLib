package edu.flash3388.flashlib.util.beans;

/**
 * A int getter and setter bean which contains a variable. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.1
 */
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
}
