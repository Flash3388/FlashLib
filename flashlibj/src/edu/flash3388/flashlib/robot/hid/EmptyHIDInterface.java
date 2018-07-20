package edu.flash3388.flashlib.robot.hid;

/**
 * Implements an empty {@link HIDInterface}. Does nothing, will return default values.
 * 
 * @author TomTzook
 * @since FlashLib 1.0.2
 */
public class EmptyHIDInterface implements HIDInterface{
	
	@Override
	public boolean isHIDConnected(int hid) {
		return false;
	}
	@Override
	public boolean isAxisConnected(int hid, int axis) {
		return false;
	}
	@Override
	public boolean isPOVConnected(int hid, int pov) {
		return false;
	}
	@Override
	public boolean isButtonConnected(int hid, int button) {
		return false;
	}
	@Override
	public double getHIDAxis(int hid, int axis) {
		return 0.0;
	}
	@Override
	public boolean getHIDButton(int hid, int button) {
		return false;
	}
	@Override
	public int getHIDPOV(int hid, int pov) {
		return -1;
	}
}
