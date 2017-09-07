package edu.flash3388.flashlib.robot.sbc;

@FunctionalInterface
public interface StateSelector {
	
	public static final byte STATE_DISABLED = 0x00;
	
	byte getState();
}
