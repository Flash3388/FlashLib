package edu.flash3388.flashlib.util.beans;

/**
 * A string getter and setter bean which contains a variable. 
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public class SimpleStringProperty implements StringProperty{

	private String var;
	
	public SimpleStringProperty(String initialVal){
		var = initialVal;
	}
	public SimpleStringProperty(){
		this("");
	}
	
	@Override
	public void set(String var){
		this.var = var;
	}
	@Override
	public String get(){
		return var;
	}
}
