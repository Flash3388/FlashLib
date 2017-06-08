package edu.flash3388.flashlib.flashboard;

public enum InputType {
	String(0), Double(1), Boolean(2);
	
	public final byte value;
	private InputType(int val){
		value = (byte) val;
	}
}
