package edu.flash3388.flashlib.robot;

import edu.flash3388.flashlib.flashboard.Flashboard;
import edu.flash3388.flashlib.flashboard.FlashboardHIDSendable;

public class FlashboardHIDInterface extends FlashboardHIDSendable implements HIDInterface{

	public FlashboardHIDInterface() {
		super("HIDInterface");
	}

	public void attachToFlashboard(){
		if(!attached() && Flashboard.flashboardInit())
			Flashboard.attach(this);
	}
	
	@Override
	public boolean isHIDConnected(int hid) {
		return getAxisCount(hid) > 0 && getButtonCount(hid) > 0;
	}
	@Override
	public boolean isAxisConnected(int hid, int axis) {
		return getAxisCount(hid) > axis;
	}
	@Override
	public boolean isPOVConnected(int hid, int pov) {
		return getPOVCount(hid) > pov;
	}
	@Override
	public boolean isButtonConnected(int hid, int button) {
		return getButtonCount(hid) >= button;
	}

	@Override
	public double getHIDAxis(int hid, int axis) {
		if(FlashRobotUtil.inEmergencyStop())
			return 0.0;
		return getAxis(hid, axis);
	}
	@Override
	public boolean getHIDButton(int hid, int button) {
		if(FlashRobotUtil.inEmergencyStop())
			return false;
		return getButton(hid, button);
	}
	@Override
	public int getHIDPOV(int hid, int pov) {
		if(FlashRobotUtil.inEmergencyStop())
			return -1;
		return getPOV(hid, pov);
	}
}
