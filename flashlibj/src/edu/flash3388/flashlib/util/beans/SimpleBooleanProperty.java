package edu.flash3388.flashlib.util.beans;

/**
 * A boolean getter and setter bean which contains a variable. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SimpleBooleanProperty implements BooleanProperty{

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
}
