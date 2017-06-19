package edu.flash3388.flashlib.flashboard;

/**
 * Represents value types for input on the Flashboard.
 * 
 * @author Tom Tzook
 * @since FlashLib 1.0.0
 */
public enum InputType {
	String(0), Double(1), Boolean(2);
	
	public final byte value;
	private InputType(int val){
		value = (byte) val;
	}
}
