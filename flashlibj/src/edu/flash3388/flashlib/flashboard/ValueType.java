package edu.flash3388.flashlib.flashboard;

public enum ValueType {
	String(0), Double(1), Boolean(2);
	
	public final byte value;
	
	private ValueType(int val){
		value = (byte) val;
	}
}
