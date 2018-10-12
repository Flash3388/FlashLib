package edu.flash3388.flashlib.robot.hid;

/**
 * Implements an empty {@link HidInterface}. Does nothing, will return default values.
 * 
 * @author TomTzook
 * @since FlashLib 1.0.2
 */
public class EmptyHidInterface implements HidInterface {
	
	@Override
	public boolean isHidConnected(int hid) {
		return false;
	}
	@Override
	public boolean isAxisConnected(int hid, int axis) {
		return false;
	}
	@Override
	public boolean isPovConnected(int hid, int pov) {
		return false;
	}
	@Override
	public boolean isButtonConnected(int hid, int button) {
		return false;
	}
	@Override
	public double getHidAxis(int hid, int axis) {
		return 0.0;
	}
	@Override
	public boolean getHidButton(int hid, int button) {
		return false;
	}
	@Override
	public int getHidPov(int hid, int pov) {
		return -1;
	}
}
