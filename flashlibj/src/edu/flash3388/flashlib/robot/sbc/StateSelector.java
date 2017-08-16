package edu.flash3388.flashlib.robot.sbc;

@FunctionalInterface
public interface StateSelector {
	
	public static final byte STATE_DISABLED = 0x00;
	public static final byte STATE_AUTONOMOUS = 0x01;
	public static final byte STATE_TELEOP = 0x02;
	
	byte getState();
}
